package otiming.fakturagrunnlag.eventor

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.ETimingDbService
import otiming.fakturagrunnlag.EventId
import otiming.fakturagrunnlag.EventorServiceImpl
import otiming.fakturagrunnlag.OtimingEventorDbRepo
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

class FetchEventorData(
    // TODO bli kvitt avhengigheten til jdbcTemplate
    val jdbcTemplate: JdbcTemplate,
    // TODO bruk AbstractEventorService istedet
    val eventorService: EventorServiceImpl,
    val eTimingDbService: ETimingDbService,
) {

    fun fetchData() {
        val eventId: EventId = eTimingDbService.findEventId()

        logger.info { "Henter data fra eventor" }
        logger.info { "entries" }
        populateOtimingEventorRawWithEntries(eventId)
        logger.info { "eventClasses" }
        populateOtimingEventorRawWithEventclasses(eventId)
        logger.info { "entryFees" }
        populateOtimingEventorRawWithEntryfees(eventId)
        logger.info { "Ferdig med å hente alle data " }
    }

    fun populateOtimingEventorRawWithEntryfees(eventId: EventId) {
        val xmlString: String? = eventorService.getEntryFeesRaw(eventId)

        xmlString?.let {
            OtimingEventorDbRepo.insertIntoOtimingEventorRaw(
                jdbcTemplate = jdbcTemplate,
                eventId = eventId,
                xmlString = it,
                // TODO gjør dette om til en enum
                endpoint = "entryfees",
                endret = LocalDateTime.now()
            )
        }
    }


    fun populateOtimingEventorRawWithEventclasses(eventId: EventId) {
        val xmlString: String? = eventorService.getEventClassesRaw(eventId)

        xmlString?.let {
            OtimingEventorDbRepo.insertIntoOtimingEventorRaw(
                jdbcTemplate = jdbcTemplate,
                eventId = eventId,
                xmlString = it,
                // TODO gjør dette om til en enum
                endpoint = "eventclasses",
                endret = LocalDateTime.now()
            )
        }
    }


    fun populateOtimingEventorRawWithEntries(eventId: EventId) {
        // TODO bedre feilhåndtering slik at det blir rapportert som feil hvis det ikke kommer xml i retur
        val xmlString: String? = eventorService.getEntriesRaw(eventId)

        xmlString?.let {
            OtimingEventorDbRepo.insertIntoOtimingEventorRaw(
                jdbcTemplate = jdbcTemplate,
                eventId = eventId,
                xmlString = it,
                // TODO gjør dette om til en enum
                endpoint = "entries",
                endret = LocalDateTime.now()
            )
        }
    }
}
