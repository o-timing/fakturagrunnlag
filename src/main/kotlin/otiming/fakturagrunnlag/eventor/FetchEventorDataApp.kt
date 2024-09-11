package otiming.fakturagrunnlag.eventor

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.ETimingDbService
import otiming.fakturagrunnlag.EventId
import otiming.fakturagrunnlag.EventorServiceImpl
import otiming.fakturagrunnlag.OTimingConfig
import otiming.fakturagrunnlag.OtimingEventorDbRepo
import java.time.LocalDateTime
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableConfigurationProperties(OTimingConfig::class)
class FetchEventorDataApp(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: OTimingConfig
) : CommandLineRunner {

    val eventorService = EventorServiceImpl(config.eventor)
    val eTimingDbService = ETimingDbService(jdbcTemplate)

    override fun run(vararg args: String?) {
        logger.info { "Databasenavn: ${config.emit.databasenavn}" }
        if (config.eventor.apiKey.isBlank()) {
            logger.error { "Eventor API-KEY is not set" }
            exitProcess(1)
        } else {
            // TODO bedre feilhåndtering slik at ikke denne slipper igjennom:
            // Eventor API-KEY: ${*********************Y}
            logger.info { "Eventor API-KEY: ${config.eventor.censoredApiKey()}" }
        }

        val eventId: EventId = findEventId(
            when (args.size) {
                1 -> args[0]
                else -> null
            }
        )

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


    fun findEventId(eventIdFromArg: String?): EventId {
        if (eventIdFromArg != null) {
            val eventId = eventIdFromArg.toInt()
            logger.info("Bruker eventId fra argument: $eventId")
            return EventId(eventId)
        } else {
            val eventIds: List<Int> = eTimingDbService.findEventIds()

            if (eventIds.isEmpty()) {
                logger.error { "Fant ingen eventId i databasen" }
                exitProcess(1)
            } else if (eventIds.size > 1) {
                logger.error { "Fant mer enn en eventId i databasen: $eventIds" }
                exitProcess(1)
            }


            val eventId = eventIds[0].toInt()
            logger.info { "eventId fra databasen: $eventId" }
            return EventId(eventId)
        }
    }

}


fun main(args: Array<String>) {
    runApplication<FetchEventorDataApp>(*args)
}
