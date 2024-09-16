package otiming.fakturagrunnlag

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.db.DbMigrations
import otiming.fakturagrunnlag.eventor.FetchEventorData
import otiming.fakturagrunnlag.eventor.PopulateEventorTables
import otiming.fakturagrunnlag.excel.ExcelReport
import otiming.fakturagrunnlag.leiebrikke.LeiebrikkeRepository
import otiming.fakturagrunnlag.leiebrikke.ReadLeiebrikkerCsv
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

            val argsLeft: List<String?> = args.drop(1)

            when (command) {
                "migrate-db" -> {
                    logger.info { "Migrate database" }
                    logger.info { "Databasenavn: ${config.etiming.databasenavn}" }
                    DbMigrations(jdbcTemplate).migrate()
                }

                "read-leiebrikker-csv" -> {
                    logger.info { "Leser leiebrikker inn i databasen fra csv-fil" }
                    logger.info { "Databasenavn: ${config.etiming.databasenavn}" }

                    if (argsLeft.isEmpty()) {
                        logger.error { "Du må spesifisere filnavn" }
                    } else {
                        val filename = argsLeft[0]!!

                        ReadLeiebrikkerCsv(jdbcTemplate).populateLeiebrikkerTable(filename)

                    }
                }

                "fetch-data-from-eventor" -> {
                    logger.info { "Fetch data from Eventor" }
                    logger.info { "Databasenavn: ${config.etiming.databasenavn}" }
                    if (config.eventor.apiKey.isBlank()) {
                        logger.error { "Eventor API-KEY is not set" }
                        exitProcess(1)
                    } else {
                        // TODO bedre feilhåndtering slik at ikke denne slipper igjennom:
                        // Eventor API-KEY: ${*********************Y}
                        logger.info { "Eventor API-KEY: ${config.eventor.censoredApiKey()}" }
                    }

                    val eventorService = EventorServiceImpl(config.eventor)
                    val eTimingDbService = ETimingDbService(jdbcTemplate)

                    FetchEventorData(jdbcTemplate, eventorService, eTimingDbService).fetchData()
                }

                "populate-eventor-tables" -> {
                    logger.info { "Populer Eventor-tabeller" }
                    logger.info { "Databasenavn: ${config.etiming.databasenavn}" }

                    val eTimingDbService = ETimingDbService(jdbcTemplate)

                    PopulateEventorTables(jdbcTemplate, eTimingDbService).parse()
                }

                "generate-excel-report" -> {
                    logger.info { "Generer Excel-rapport" }
                    logger.info { "Databasenavn: ${config.etiming.databasenavn}" }

                    val otimingFakturaRapport = OtimingFakturaRapport(jdbcTemplate)
                    val leiebrikkeRepository = LeiebrikkeRepository(jdbcTemplate)

                    ExcelReport(otimingFakturaRapport, leiebrikkeRepository).fakturagrunnlagExcel(config.etiming.databasenavn)
                }
            }
        }
    }
}


fun main(args: Array<String>) {
    runApplication<OTimingFakturagrunnlag>(*args)
}
