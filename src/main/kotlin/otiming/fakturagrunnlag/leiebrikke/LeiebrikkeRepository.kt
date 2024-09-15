package otiming.fakturagrunnlag.leiebrikke

import okhttp3.internal.toImmutableList
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.Brikkenummer
import otiming.fakturagrunnlag.OtimingDomain

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

    fun getLeiebrikker(): List<LeiebrikkeRow> {
        return jdbcTemplate.query(
            """ 
                select kortnavn, eier, brikkenummer
                from otiming_leiebrikker
            """.trimIndent()
        ) { rs, _ ->
            LeiebrikkeRow(
                Brikkenummer(rs.getString("brikkenummer")),
                rs.getString("eier"),
                rs.getString("kortnavn")
            )
        }.toImmutableList()
    }
}

data class LeiebrikkeRow(
    val brikkenummer: Brikkenummer,
    val eier: String,
    val kortnavn: String
)