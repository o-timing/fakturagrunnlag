package otiming.fakturagrunnlag

import generated.EntryFee
import generated.EntryFeeList
import generated.EntryList
import generated.EventClass
import generated.EventClassList
import jakarta.xml.bind.JAXBContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.io.StringReader
import java.time.LocalDateTime

@SpringBootTest
class PopulateEventorTablesTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: otiming.fakturagrunnlag.EventorConfig
) {

    val eventId: EventId = EventId(19104)

    @Test
    fun populateOtimingEventorEntry() {
        // les entryfees fra raw tabellen
        val rawRow: OtimingEventorRawRow? = lesFraOtimingEventorRaw(eventId, "entries")

        rawRow?.let { row ->
            // parse som xml
            val entryList = xmlStringAs<EntryList>(row.xmlString)

            // iterer over og skriv hver enkelt rad til otiming_eventor_entryfees
            entryList.entry.forEach { e ->
                val entryId = e.entryId.toInternalId()
                insertIntoOtimingEventorEntry(
                    entryId = entryId,
                    personId = e.competitor.person.personId.toInternalId(),
                    eventId = e.eventId.toInternalId()
                )
                e.competitor.cCard.forEach { cc ->
                    insertIntoOtimingEventorEntryCCard(
                        entryId = entryId,
                        ccardId = cc.cCardId.content.toInt(),
                        ccardType = cc.punchingUnitType.value
                    )
                }
                e.entryClass.forEach { ec ->
                    insertIntoOptimingEventorEntryEventClass(
                        entryId = entryId,
                        eventClassId = ec.eventClassId.toInternalId()
                    )
                }
                e.entryEntryFee.forEach { ef ->
                    insertIntoOptimingEventorEntryEntryFee(
                        entryId = entryId,
                        entryFeeId = ef.entryFeeId.toInternalId(),
                        sequence = ef.sequence.content.toInt()
                    )
                }
            }
        }
    }


    fun insertIntoOtimingEventorEntry(entryId: EntryId, personId: PersonId, eventId: EventId) {
        jdbcTemplate.update(
            """insert into otiming_eventor_entry (entryId, personId, eventId) 
               values (?, ?, ?)
            """.trimMargin(),
            entryId.value,
            personId.value,
            eventId.value
        )
    }

    fun insertIntoOtimingEventorEntryCCard(entryId: EntryId, ccardId: Int, ccardType: String) {
        jdbcTemplate.update(
            """insert into otiming_eventor_entry_ccard (entryId, ccardId, ccardType) 
                values (?, ?, ?)
            """.trimMargin(),
            entryId.value,
            ccardId,
            ccardType
        )
    }

    fun insertIntoOptimingEventorEntryEventClass(entryId: EntryId, eventClassId: EventClassId) {
        jdbcTemplate.update(
            """insert into otiming_eventor_entry_eventclass (entryId, eventClassId) 
               values (?, ?)
            """.trimMargin(),
            entryId.value,
            eventClassId.value
        )
    }

    fun insertIntoOptimingEventorEntryEntryFee(entryId: EntryId, entryFeeId: EntryFeeId, sequence: Int) {
        jdbcTemplate.update(
            """insert into otiming_eventor_entry_entryfee (entryId, entryFeeId, sequence) 
               values (?, ?, ?)
            """.trimMargin(),
            entryId.value,
            entryFeeId.value,
            sequence
        )
    }

    @Test
    fun populateOtimingEventorEventclasses() {
        // les entryfees fra raw tabellen
        val rawRow: OtimingEventorRawRow? = lesFraOtimingEventorRaw(eventId, "eventclasses")

        rawRow?.let { row ->
            // parse som xml
            val eventClassList = xmlStringAs<EventClassList>(row.xmlString)

            // iterer over og skriv hver enkelt rad til otiming_eventor_entryfees
            eventClassList.eventClass.forEach { e: EventClass ->
                insertIntoOtimingEventorEventclass(
                    eventId = eventId,
                    eventClassId = e.eventClassId.content.toInt(),
                    name = e.name.content,
                    shortName = e.classShortName.content
                )
                e.classEntryFee.forEach { cef ->
                    insertIntoOtimingEventorEventclassEntryfee(
                        eventClassId = e.eventClassId.content.toInt(),
                        entryFeeId = cef.entryFeeId.content.toInt(),
                        sequence = cef.sequence.content.toInt()
                    )
                }
            }
        }
    }

    fun insertIntoOtimingEventorEventclass(eventId: EventId, eventClassId: Int, name: String, shortName: String) {
        jdbcTemplate.update(
            """
                insert into otiming_eventor_eventclass (eventClassId, eventId, name, shortName)
                values (?, ?, ?, ?)
            """.trimIndent(),
            eventClassId,
            eventId.value,
            name,
            shortName
        )
    }

    fun insertIntoOtimingEventorEventclassEntryfee(eventClassId: Int, entryFeeId: Int, sequence: Int) {
        jdbcTemplate.update(
            """
                insert into otiming_eventor_eventclass_entryfee (eventClassId, entryFeeId, sequence)
                values (?, ?, ?)
            """.trimIndent(),
            eventClassId, entryFeeId, sequence
        )
    }


    @Test
    fun populateOtimingEventorEntryfees() {
        // les entryfees fra raw tabellen
        val rawRow: OtimingEventorRawRow? = lesFraOtimingEventorRaw(eventId, "entryfees")

        rawRow?.let { row ->
            // parse som xml
            val entryfeelist = xmlStringAs<EntryFeeList>(row.xmlString)

            // iterer over og skriv hver enkelt rad til otiming_eventor_entryfees
            entryfeelist.entryFee.forEach { e: EntryFee ->
                insertIntoTilOtimingEventorEntryfee(
                    eventId,
                    e
                )
            }
        }
    }

    private inline fun <reified T> xmlStringAs(xmlString: String): T {
        val context = JAXBContext.newInstance(T::class.java)
        val stringReader = StringReader(xmlString)
        val unmarshaller = context.createUnmarshaller()
        return unmarshaller.unmarshal(stringReader) as T
    }


    // TODO fiks denne til å returnere en liste
    fun lesFraOtimingEventorRaw(eventId: EventId, endpoint: String): OtimingEventorRawRow? {
        return jdbcTemplate.queryForObject<OtimingEventorRawRow>(
            """select eventId, endpoint, endret, xml
                | from otiming_eventor_raw
                | where eventId = ?
                | and endpoint = ?
            """.trimMargin(),
            arrayOf(eventId.value, endpoint)
        ) { response, _ ->
            OtimingEventorRawRow(
                EventId(response.getInt("eventId")),
                response.getString("endpoint"),
                response.getObject("endret", LocalDateTime::class.java),
                response.getString("xml"),
            )
        }
    }

    fun insertIntoTilOtimingEventorEntryfee(eventId: EventId, entryfee: EntryFee) {
        jdbcTemplate.update(
            """
                insert into otiming_eventor_entryfee (entryFeeId, eventId, name, amount)
                values (?, ?, ?, ?)
            """.trimIndent(),
            entryfee.entryFeeId.content.toInt(), eventId.value, entryfee.name.content, entryfee.amount.content.toInt()
        )
    }

    data class OtimingEventorRawRow(
        val eventId: EventId,
        val endpoint: String,
        val endret: LocalDateTime,
        val xmlString: String,
    )
}

