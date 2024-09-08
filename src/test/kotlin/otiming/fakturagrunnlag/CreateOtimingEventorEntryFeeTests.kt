package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.db.CheckIfTableExists
import otiming.fakturagrunnlag.db.DbMigrations

@SpringBootTest
class CreateOtimingEventorEntryFeeTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    val dbMigrations = DbMigrations(jdbcTemplate)

    @Test
    fun createOtimingEventorEntryFeeTest() {
        dbMigrations.createOtimingEventorEntryfeeTable()
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_entryfee"))
    }


}