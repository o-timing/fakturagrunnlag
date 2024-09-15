package otiming.fakturagrunnlag.db

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.OTimingConfig

private val logger = KotlinLogging.logger {}

//@SpringBootApplication
//@EnableConfigurationProperties(OTimingConfig::class)
class DbMigrationsApp(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: OTimingConfig
) : CommandLineRunner {

    val dbMigrations = DbMigrations(jdbcTemplate)

    override fun run(vararg args: String?) {
        logger.info { "Migrate database" }
        logger.info { "Databasenavn: ${config.emit.databasenavn}" }

        dbMigrations.migrate()
    }
}

fun main(args: Array<String>) {
    runApplication<DbMigrationsApp>(*args)
}

