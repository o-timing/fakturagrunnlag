package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class CreateOtimingEventorEntryTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun createOtimingEventorEntryTest() {
        // sjekk om tabellen finnes
        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorEntryTable()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry"))

        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_entryfee")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorEntryEntryFee()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_entryfee"))

        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_ccard")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorEntryCCard()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_ccard"))

        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_eventclass")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorEntryEventClass()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_eventclass"))
    }

    fun createOtimingEventorEntryTable() {
        jdbcTemplate.execute(
            """
                CREATE TABLE otiming_eventor_entry
                (
                    entryId INT PRIMARY KEY,
                    personId INT,
                    eventId INT
                );
    """.trimIndent()
        )
    }

    fun createOtimingEventorEntryEntryFee() {
        jdbcTemplate.execute(
            """
                CREATE TABLE otiming_eventor_entry_entryfee
                (
                    entryId INT NOT NULL,
                    entryFeeId INT NOT NULL,
                    sequence INT NOT NULL,
                );
    """.trimIndent()
        )
    }

    fun createOtimingEventorEntryCCard() {
        jdbcTemplate.execute(
            """
                CREATE TABLE otiming_eventor_entry_ccard
                (
                    entryId INT NOT NULL,
                    ccardId INT NOT NULL,
                    ccardType VARCHAR(10) NOT NULL
                );
    """.trimIndent()
        )
    }

    fun createOtimingEventorEntryEventClass() {
        jdbcTemplate.execute(
            """
                CREATE TABLE otiming_eventor_entry_eventclass
                (
                    entryId INT NOT NULL,
                    eventClassId INT NOT NULL
                );
    """.trimIndent()
        )
    }


}