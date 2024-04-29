package com.example.otiming

import okhttp3.internal.toImmutableList
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class BasicReportTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun fooTest() {
        val foo: Map<Int, List<BasicReport>> = selectBasicReport().groupBy { it.id }

        println(foo)
    }

    fun selectBasicReport(): List<BasicReport> {
        return jdbcTemplate.query(
            """ 
                select name.id,
                       team.name as klubb,
                       arr.NAME as distanse,
                       CONVERT(DATE, day.firststart) as dato,
                       name.startno as startnr,
                       name.name as fornavn,
                       name.ename as etternavn,
                       otiming_eventor_eventclass.name as eventor_class_name
                from name
                         join team on (name.team = team.code)
                         join arr on (name.arr = arr.arr and name.sub = arr.SUB)
                         join day on (arr.SUB = day.day)
                         join class on (name.class = class.code)
                         join otiming_eventor_eventclass on (name.class = otiming_eventor_eventclass.eventClassId)
            """.trimIndent()
        ) { rs, _ ->
            BasicReport(
                id = rs.getInt("id"),
                klubb = rs.getString("klubb"),
                distanse = rs.getString("distanse").trim(),
                dato = rs.getString("dato").trim(),
                startnr = rs.getString("startnr")?.trim(),
                fornavn = rs.getString("fornavn").trim(),
                etternavn = rs.getString("etternavn").trim(),
                klasse = rs.getString("eventor_class_name").trim()
            )
        }.toImmutableList()
    }

}

data class BasicReport(
    val id: Int,
    val klubb: String,
    val distanse: String,
    val dato: String,
    val startnr: String?,
    val fornavn: String,
    val etternavn: String,
    val klasse: String
)
