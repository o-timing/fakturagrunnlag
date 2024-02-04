package com.example.otiming

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.xml.bind.JAXBContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.toImmutableList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component
import java.io.StringReader
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableConfigurationProperties(EventorConfig::class)
class OTimingApplication(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: EventorConfig
) : CommandLineRunner {

    val eventorService = EventorService(config)

    // TODO:
    // - skrive test som ikke krever at vi kontakter eventor
    //   - lagre output fra eventor-spørringer til fil i test
    //     - entryfees.xml
    //     - entries.xml
    //     - eventclasses.xml
    // - skriv til database

    override fun run(vararg args: String?) {
        val eventIds = findEventIds()

//        val entries = fetchEntries(eventId)
//        log.info (entries)
//        val entryFees = getEntryFeeList(eventId)
//        log.info (entryFees)
//        val eventclasses = getEventClasses(eventId)
//        log.info (eventclasses)
    }

    private fun findEventIds(): List<Int> {
        logger.info { "getting eventId from database" }
        val rowMapper: RowMapper<Int> = RowMapper<Int> { resultSet: ResultSet, _: Int ->
            resultSet.getInt("id")
        }


        val results = jdbcTemplate.query(
            """
                select id
                from day
            """.trimMargin(),
            rowMapper
        )

        logger.info { "Got eventIds from database: ${results.joinToString(", ")}" }

        return results.toImmutableList()
    }
}

fun main(args: Array<String>) {
    runApplication<OTimingApplication>(*args)
}
