package com.example.otiming

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.internal.toImmutableList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableConfigurationProperties(OTimingConfig::class)
class OTimingApplication(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: OTimingConfig
) : CommandLineRunner {

    val eventorService = EventorServiceImpl(config.eventor)
    val eTimingDbService = ETimingDbService(jdbcTemplate)

    // TODO:
    // - skrive test som ikke krever at vi kontakter eventor
    // - skriv til database

    override fun run(vararg args: String?) {
        // TODO skriv ut config
        logger.info { "Databasenavn: ${config.emit.database}" }
        if (config.eventor.apiKey.isNullOrEmpty()) {
            logger.error { "Eventor API-KEY is not set"}
        } else {
            logger.info { "Eventor API-KEY: ${config.eventor.censoredApiKey()}" }
        }

        val eventIds = eTimingDbService.findEventIds()
        val eventId = eventIds[0]

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
    runApplication<OTimingApplication>(*args)
}
