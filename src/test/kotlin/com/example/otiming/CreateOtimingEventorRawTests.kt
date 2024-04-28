package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class CreateOtimingEventorRawTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun createOtiminEventorRawTest() {
        // sjekk om tabellen finnes
        if (!finnesOtimingEventorRawtabellen()) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorRawTable()
        }
        assert(finnesOtimingEventorRawtabellen())
    }

    /**
     * Sjekker om otiming_eventor_raw-tabellen finnes i databasen
     *
     * @return true hvis den finnes, false hvis den ikke finnes
     */
    fun finnesOtimingEventorRawtabellen(): Boolean {
        val count: Int = jdbcTemplate.queryForObject(
            """
                SELECT count(*)
                FROM sys.tables
                WHERE name = 'otiming_eventor_raw' 
                  AND schema_name(schema_id) = 'dbo'
        """.trimIndent(),
            Integer::class.java
        )!!.toInt()
        return count == 1
    }

    fun createOtimingEventorRawTable() {
        jdbcTemplate.execute(
            """
                CREATE TABLE otiming_eventor_raw
                (
                    eventId  INT           NOT NULL
                        CONSTRAINT otiming_eventor_raw_pk PRIMARY KEY,
                    endpoint VARCHAR(100)  NOT NULL,
                    xml      NVARCHAR(MAX) NOT NULL,
                    hentet   DATETIME2     NOT NULL
                );
            """.trimIndent()
        )
    }

}