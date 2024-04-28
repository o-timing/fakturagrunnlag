package com.example.otiming

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

class EventorDbRepo(val jdbcTemplate: JdbcTemplate) {
    fun insertEventorRawData(eventId: Int, xmlString: String, endpoint: String, hentet: LocalDateTime) {
        jdbcTemplate.update(
            """insert into otiming_eventor_raw (eventId, xml, endpoint, hentet) values (?, ?, ?, ?) """.trimIndent(),
            eventId, xmlString, endpoint, hentet
        )
    }
}