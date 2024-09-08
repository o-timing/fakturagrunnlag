package otiming.fakturagrunnlag

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.db.DbMigrations

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableConfigurationProperties(OTimingConfig::class)
class OTimingFakturagrunnlag(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: OTimingConfig
) : CommandLineRunner {

    val eventorService = EventorServiceImpl(config.eventor)
    val eTimingDbService = ETimingDbService(jdbcTemplate)

    val dbMigrations = DbMigrations(jdbcTemplate)

    // TODO:
    // - skrive test som ikke krever at vi kontakter eventor
    // - skriv til database

    override fun run(vararg args: String?) {
        // TODO skriv ut config
        logger.info { "Databasenavn: ${config.emit.databasenavn}" }
        if (config.eventor.apiKey.isBlank()) {
            logger.error { "Eventor API-KEY is not set" }
        } else {
            logger.info { "Eventor API-KEY: ${config.eventor.censoredApiKey()}" }
        }

        dbMigrations.migrate()

        val eventIds = eTimingDbService.findEventIds()
        if (eventIds.isEmpty()) logger.error { "Fant ingen eventId i databasen" }
        else if (eventIds.size > 1) logger.error { "Fant mer enn en eventId i databasen: $eventIds"}

        val eventId = eventIds[0]
        logger.info { "eventId fra databasen: $eventId" }

//        val entries = eventorService.getEntries(eventId)
//        logger.info { entries }
//
//        val entryFees = eventorService.getEntryFees(eventId)
//        logger.info { entryFees }
//
//        val eventclasses = eventorService.getEventClasses(eventId)
//        logger.info { eventclasses }
    }

}


fun main(args: Array<String>) {
    runApplication<OTimingFakturagrunnlag>(*args)
}
