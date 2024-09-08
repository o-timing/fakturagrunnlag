package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.db.CheckIfTableExists
import otiming.fakturagrunnlag.db.DbMigrations

@SpringBootTest
class CreateOtimingEventorEntryTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    val dbMigrations = DbMigrations(jdbcTemplate)

    @Test
    fun createOtimingEventorEntryTest() {
        dbMigrations.createOtimingEventorEntryTables()

        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry"))
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_entryfee"))
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_ccard"))
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_eventclass"))
    }

}