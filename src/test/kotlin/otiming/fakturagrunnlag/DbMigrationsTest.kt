package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.db.CheckIfTableExists
import otiming.fakturagrunnlag.db.DbMigrations

@SpringBootTest
class DbMigrationsTest(@Autowired val jdbcTemplate: JdbcTemplate) {

    val dbMigrations = DbMigrations(jdbcTemplate)

    @Test
    fun createOtimingEventorEntryFeeTest() {
        dbMigrations.createOtimingEventorEntryfeeTable()
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entryfee"))
    }

    @Test
    fun createOtimingEventorEntryTest() {
        dbMigrations.createOtimingEventorEntryTables()

        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry"))
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_entryfee"))
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_ccard"))
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entry_eventclass"))
    }

    @Test
    fun createOtimingEventorEventclassTest() {
        dbMigrations.createOtimingEventorEventclassTables()

        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_eventclass"))
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_eventclass_entryfee"))
    }

    @Test
    fun createOtimingEventorRawTest() {
        dbMigrations.createOtimingEventorRawTable()

        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_raw"))
    }

    @Test
    fun createOtimingLeiebrikkerTest() {
        dbMigrations.createOtimingLeiebrikkerTable()

        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_leiebrikker"))
    }


}