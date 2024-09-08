package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.db.CheckIfTableExists
import otiming.fakturagrunnlag.db.DbMigrations

@SpringBootTest
class CreateOtimingEventorRawTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {
    val dbMigrations = DbMigrations(jdbcTemplate)

    @Test
    fun createOtimingEventorRawTest() {
        dbMigrations.createOtimingEventorRawTable()
        assert(CheckIfTableExists.finnesTabell(jdbcTemplate, "otiming_eventor_raw"))
    }

}

