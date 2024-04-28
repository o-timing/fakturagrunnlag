package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class CreateOtimingEventorEntryfeesTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun createOtiminEventorRawTest() {
        // sjekk om tabellen finnes
        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entryfee")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorEntryfeesTable()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entryfee"))
    }

    fun createOtimingEventorEntryfeesTable() {
        jdbcTemplate.execute(
            """
                CREATE TABLE otiming_eventor_entryfee
                (
                    entryFeeId INT PRIMARY KEY,
                    eventId    INT,
                    name       NVARCHAR(100),
                    amount     INT
                );
    """.trimIndent()
        )
    }

}