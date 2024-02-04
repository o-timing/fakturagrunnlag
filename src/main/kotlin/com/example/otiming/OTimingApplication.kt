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
@EnableConfigurationProperties(EventorConfig::class)
class OTimingApplication(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: EventorConfig
) : CommandLineRunner {

    val eventorService = EventorServiceImpl(config)
    val eTimingDbService = ETimingDbService(jdbcTemplate)

    // TODO:
    // - skrive test som ikke krever at vi kontakter eventor
    // - skriv til database

    override fun run(vararg args: String?) {
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
