package otiming.fakturagrunnlag.leiebrikke

import org.springframework.jdbc.core.JdbcTemplate

class LeiebrikkeRepository(val jdbcTemplate: JdbcTemplate) {
    fun insertIntoOtimingLeiebrikker(row: LeiebrikkeCsvRow) {
        jdbcTemplate.update(
            """
                insert into otiming_leiebrikker (brikkenummer, eier, kortnavn, kommentar)
                values (?, ?, ?, ?)
            """.trimIndent(),
            row.brikkenummer.value.toInt(),
            row.eier,
            row.kortnavn,
            row.kommentar
        )
    }
}
