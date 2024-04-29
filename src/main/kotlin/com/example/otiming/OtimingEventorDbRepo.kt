package com.example.otiming

import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

object OtimingEventorDbRepo {
    fun insertIntoOtimingEventorRaw(jdbcTemplate: JdbcTemplate, eventId: EventId, xmlString: String, endpoint: String, endret: LocalDateTime) {
        jdbcTemplate.update(
            """insert into otiming_eventor_raw (eventId, endpoint, endret, xml) values (?, ?, ?, ?) """.trimIndent(),
            eventId.value, endpoint, endret, xmlString
        )
    }

}