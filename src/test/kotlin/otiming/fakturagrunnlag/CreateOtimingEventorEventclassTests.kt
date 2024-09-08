package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class CreateOtimingEventorEventclassTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun createOtimingEventorEventclassTest() {
        // sjekk om tabellen finnes
        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_eventclass")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorEventclassesTable()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_eventclass"))

        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_eventclass_entryfee")) {
            createOtimingEventorEventclassEntryfeeTable()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_eventclass_entryfee"))
    }

    fun createOtimingEventorEventclassesTable() {
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

    fun createOtimingEventorEventclassEntryfeeTable() {
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