package com.example.otiming

import com.example.otiming.OtimingDomain.KontigentRapportLinje
import com.example.otiming.OtimingDomain.LeiebrikkeRapportLinje
import com.example.otiming.OtimingDomain.BasisRapportLinje
import okhttp3.internal.toImmutableList
import org.springframework.jdbc.core.JdbcTemplate

class OtimingFakturaRapport(val jdbcTemplate: JdbcTemplate) {

    fun selectBasicReport(): List<BasisRapportLinje> {
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
            BasisRapportLinje(
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

    fun selectLeiebrikkeRapport(): Map<Int, LeiebrikkeRapportLinje> {
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
            LeiebrikkeRapportLinje(
                id = rs.getInt("id"),
                rs.getInt("ecard"),
                rs.getInt("ecard2"),
                rs.getInt("etiming_ecard"),
                rs.getBoolean("etiming_ecardfee"),
                rs.getInt("leiebrikke_nummer"),
                rs.getString("leiebrikke_eier"),
                rs.getString("leiebrikke_kortnavn")
            )
        }.groupBy { it.id }.mapValues { entry ->
            entry.value.first()
        }
    }

    fun selectKontigentRapport(): Map<Int, KontigentRapportLinje> {
        return jdbcTemplate.query(
            """select name.id,
                       class.entryfee1                    as etiming_entryfee1,
                       class.entryfee2                    as etiming_entryfee2,
                       class.entryfee3                    as etiming_entryfee3,
                       STRING_AGG(entryfee.name, ', ')    as eventor_entryfee_names,
                       STRING_AGG(entryfee.amount, ' + ') as eventor_entryfee_calculation,
                       SUM(entryfee.amount)               as eventor_entryfee_sum
                from name
                         join class on (name.class = class.code)
                         left join otiming_eventor_entry entry on (name.kid = entry.personId)
                         left join otiming_eventor_entry_entryfee entry_entryfee on (entry.entryId = entry_entryfee.entryId)
                         left join otiming_eventor_entryfee entryfee on (entry_entryfee.entryFeeId = entryfee.entryFeeId)
                group by name.id, class.entryfee1, class.entryfee2, class.entryfee3""".trimIndent()
        ) { rs, _ ->
            KontigentRapportLinje(
                id = rs.getInt("id"),
                rs.getDouble("etiming_entryfee1"),
                rs.getDouble("etiming_entryfee2"),
                rs.getDouble("etiming_entryfee3"),
                rs.getString("eventor_entryfee_names"),
                rs.getString("eventor_entryfee_calculation"),
                rs.getDouble("eventor_entryfee_sum")
            )
        }.groupBy { it.id }.mapValues { entry ->
            entry.value.first()
        }
    }

}