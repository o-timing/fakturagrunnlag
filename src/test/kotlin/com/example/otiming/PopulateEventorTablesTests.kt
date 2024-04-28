package com.example.otiming

import generated.EntryFee
import generated.EntryFeeList
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
    @Autowired val config: EventorConfig
) {

    val eventId = 19449

    @Test
    fun populateOtimingEventorRawWithEntryfees() {
        val eventorService = EventorServiceImpl(config)

        val xmlString: String? = eventorService.getEntryFeesRaw(eventId)

        xmlString?.let {
            OtimingEventorDbRepo.insertIntoOtimingEventorRaw(
                jdbcTemplate,
                eventId,
                it,
                "entryfees",
                LocalDateTime.now()
            )
        }
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
                skrivTilOtimingEventorEntryfees(
                    eventId,
                    e,
                    row.endret
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


    fun lesFraOtimingEventorRaw(eventId: Int, endpoint: String): OtimingEventorRawRow? {
        return jdbcTemplate.queryForObject<OtimingEventorRawRow>(
            """select eventId, endpoint, endret, xml
                | from otiming_eventor_raw
                | where eventId = ?
                | and endpoint = ?
            """.trimMargin(),
            arrayOf(eventId, endpoint)
        ) { response, _ ->
            OtimingEventorRawRow(
                response.getInt("eventId"),
                response.getString("endpoint"),
                response.getObject("endret", LocalDateTime::class.java),
                response.getString("xml"),
            )
        }
    }

    fun skrivTilOtimingEventorEntryfees(eventId: Int, entryfee: EntryFee, endret: LocalDateTime) {
        jdbcTemplate.update(
            """
                insert into otiming_eventor_entryfees (entryFeeId, endret, eventId, name, amount)
                values (?, ?, ?, ?, ?)
            """.trimIndent(),
            entryfee.entryFeeId.content.toInt(), endret, eventId, entryfee.name.content, entryfee.amount.content.toInt()
        )
    }

    data class OtimingEventorRawRow(
        val eventId: Int,
        val endpoint: String,
        val endret: LocalDateTime,
        val xmlString: String,
    )
}

