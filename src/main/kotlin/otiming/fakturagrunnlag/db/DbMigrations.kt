package otiming.fakturagrunnlag.db

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.jdbc.core.JdbcTemplate

private val logger = KotlinLogging.logger {}

class DbMigrations(val jdbcTemplate: JdbcTemplate) {

    fun migrate() {
        logger.info { "Kjører migreringer i databasen" }
        createOtimingEventorRawTable()
        createOtimingEventorEntryTables()
        createOtimingEventorEventclassTables()
        createOtimingEventorEntryfeeTable()
    }

    /**
     * Oppretter otiming_eventor_raw hvis den ikke finnes fra før
     */
    fun createOtimingEventorRawTable() {
        createTableIfNotExists("otiming_eventor_raw") {
            jdbcTemplate.execute(
                """
                CREATE TABLE otiming_eventor_raw
                (
                    eventId  INT           NOT NULL,
                    endpoint VARCHAR(100)  NOT NULL,
                    endret   DATETIME2     NOT NULL,
                    xml      NVARCHAR(MAX) NOT NULL,
                    CONSTRAINT otiming_eventor_raw_pk
                        PRIMARY KEY (eventId, endpoint, endret)
                )
            """.trimIndent()
            )
        }
    }

    fun createOtimingEventorEntryTables() {
        createTableIfNotExists("otiming_eventor_entry") {
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

        createTableIfNotExists("otiming_eventor_entry_entryfee") {
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

        createTableIfNotExists("otiming_eventor_entry_ccard") {
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

        createTableIfNotExists("otiming_eventor_entry_eventclass") {
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

    fun createOtimingEventorEventclassTables() {
        createTableIfNotExists("otiming_eventor_eventclass") {
            jdbcTemplate.execute(
                """
                CREATE TABLE otiming_eventor_eventclass
                (
                    eventClassId INT NOT NULL PRIMARY KEY, 
                    eventId    INT NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    shortName VARCHAR(50) NOT NULL
                );
                """.trimIndent()
            )
        }

        createTableIfNotExists("otiming_eventor_eventclass_entryfee") {
            jdbcTemplate.execute(
                """
                CREATE TABLE otiming_eventor_eventclass_entryfee
                (
                    eventClassId INT NOT NULL,
                    entryFeeId INT NOT NULL,
                    sequence INT NOT NULL,
                    
                    constraint otiming_eventor_eventclass_entryfee_pk 
                        primary key (eventClassId, entryFeeId, sequence) 
                );
                """.trimIndent()
            )
        }
    }

    fun createOtimingEventorEntryfeeTable() {
        createTableIfNotExists("otiming_eventor_entryfee") {
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

    private fun createTableIfNotExists(tableName: String, action: () -> Unit) {
        if (CheckIfTableExists.finnesTabell(jdbcTemplate, tableName)) {
            logger.info { "$tableName finnes fra før" }
        } else {
            logger.info { "oppretter $tableName" }
            action()
        }
    }

}