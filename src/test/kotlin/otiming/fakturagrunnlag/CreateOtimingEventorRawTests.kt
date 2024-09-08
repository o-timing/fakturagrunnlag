package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class CreateOtimingEventorRawTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun createOtimingEventorRawTest() {
        // sjekk om tabellen finnes
        if (!CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_raw")) {
            // hvis den ikke finnes opprett tabellen
            createOtimingEventorRawTable()
        }
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_raw"))
    }

    fun createOtimingEventorRawTable() {
        jdbcTemplate.execute(
            """
                create table otiming_eventor_raw
                (
                    eventId  int           not null,
                    endpoint varchar(100)  not null,
                    endret   datetime2     not null,
                    xml      nvarchar(max) not null,
                    constraint otiming_eventor_raw_pk
                        primary key (eventId, endpoint, endret)
                )
            """.trimIndent()
        )
    }

}