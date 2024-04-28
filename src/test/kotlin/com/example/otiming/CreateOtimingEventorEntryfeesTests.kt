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
        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entryfees")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorEntryfeesTable()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entryfees"))
    }

    fun createOtimingEventorEntryfeesTable() {
        jdbcTemplate.execute(
            """
                CREATE TABLE otiming_eventor_entryfees
                (
                    entryFeeId INT,
                    endret     DATETIME2 NOT NULL,
                    eventId    INT,
                    name       NVARCHAR(100),
                    amount     INT,
                    
                    constraint otiming_eventor_entryfees_pk 
                        primary key (entryFeeId, endret) 
                );
    """.trimIndent()
        )
    }

}