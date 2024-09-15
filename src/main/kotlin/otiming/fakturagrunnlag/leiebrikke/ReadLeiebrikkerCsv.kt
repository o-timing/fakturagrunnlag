package otiming.fakturagrunnlag.leiebrikke

import org.springframework.jdbc.core.JdbcTemplate

class ReadLeiebrikkerCsv(jdbcTemplate: JdbcTemplate) {
    val leiebrikkeRepository = LeiebrikkeRepository(jdbcTemplate)

    fun populateLeiebrikkerTable(leiebrikkeFilename: String) {
        val leiebrikker: List<LeiebrikkeCsvRow> = LeiebrikkeCsvRepository.readLeiebrikkeCsv(leiebrikkeFilename)

        leiebrikker.forEach {
            leiebrikkeRepository.insertIntoOtimingLeiebrikker(it)
        }
    }
}