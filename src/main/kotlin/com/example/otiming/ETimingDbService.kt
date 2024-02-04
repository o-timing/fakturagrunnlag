package com.example.otiming

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.internal.toImmutableList
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

class ETimingDbService(val jdbcTemplate: JdbcTemplate) {

    fun findEventIds(): List<Int> {
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
