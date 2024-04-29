package com.example.otiming

import okhttp3.internal.toImmutableList
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class LeiebrikkeReportTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun fooTest() {
        val foo: Map<Int, List<LeiebrikkeReport>> = selectLeiebrikkeReport().groupBy{ it.id}
        println(foo)
    }

    fun selectLeiebrikkeReport(): List<LeiebrikkeReport> {
        return jdbcTemplate.query(
            """with etiming_ecard as (select name.id,
                                          name.ecard,
                                          name.ecard2,
                                          coalesce(name.ecard, name.ecard2) as etiming_ecard,
                                          name.ecardfee                     as etiming_ecardfee
                                       from name)
                select name.id,
                       etiming_ecard.ecard,
                       etiming_ecard.ecard2,
                       etiming_ecard.etiming_ecard,
                       etiming_ecard.etiming_ecardfee,
                       otiming_leiebrikker.brikkenummer as leiebrikke_nummer,
                       otiming_leiebrikker.eier         as leiebrikke_eier,
                       otiming_leiebrikker.kortnavn     as leiebrikke_kortnavn
                from name
                     join etiming_ecard on (name.id = etiming_ecard.id)
                     left join otiming_leiebrikker on (otiming_leiebrikker.brikkenummer = etiming_ecard.ecard)
            """.trimIndent()
        ) { rs, _ ->
            LeiebrikkeReport(
                id = rs.getInt("id"),
                rs.getInt("ecard"),
                rs.getInt("ecard2"),
                rs.getInt("etiming_ecard"),
                rs.getBoolean("etiming_ecardfee"),
                rs.getInt("leiebrikke_nummer"),
                rs.getString("leiebrikke_eier"),
                rs.getString("leiebrikke_kortnavn")
            )
        }.toImmutableList()
    }

}

data class LeiebrikkeReport(
    val id: Int,
    val ecard1: Int,
    val ecard2: Int,
    val etimingEcard: Int,
    val etimingEcardFee: Boolean,
    val leiebrikke_nummer: Int?,
    val leiebrikke_eier: String?,
    val leiebrikke_kortnavn: String?,
)
