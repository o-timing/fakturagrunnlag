package otiming.fakturagrunnlag

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.db.DbMigrations
import otiming.fakturagrunnlag.eventor.FetchEventorDataApp
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

@SpringBootApplication
@EnableConfigurationProperties(OTimingConfig::class)
class OTimingFakturagrunnlag(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: OTimingConfig
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (args.isEmpty()) {
            // TODO: usage
            logger.error { "Trenger et argument for å starte" }
        } else {
            val command = args[0]!!

            when (command) {
                "migrate-db" -> {
                    logger.info { "Migrate database" }
                    logger.info { "Databasenavn: ${config.emit.databasenavn}" }
                    DbMigrations(jdbcTemplate).migrate()
                }

                "fetch-data-from-eventor" -> {
                    logger.info { "Fetch data from Eventor" }
                    logger.info { "Databasenavn: ${config.emit.databasenavn}" }
                    if (config.eventor.apiKey.isBlank()) {
                        logger.error { "Eventor API-KEY is not set" }
                        exitProcess(1)
                    } else {
                        // TODO bedre feilhåndtering slik at ikke denne slipper igjennom:
                        // Eventor API-KEY: ${*********************Y}
                        logger.info { "Eventor API-KEY: ${config.eventor.censoredApiKey()}" }
                    }


                    FetchEventorDataApp(jdbcTemplate, config).fetchData(args.drop(1))
                }
            }
        }
    }
}


fun main(args: Array<String>) {
    runApplication<OTimingFakturagrunnlag>(*args)
}
