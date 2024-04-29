package com.example.otiming

import com.example.otiming.OtimingDomain.KontigentRapportLinje
import org.springframework.jdbc.core.JdbcTemplate

class OtimingFakturaRapport(val jdbcTemplate: JdbcTemplate) {

    fun selectKontigentRapport(): Map<Int, List<KontigentRapportLinje>> {
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
        }.groupBy { it.id }
    }

}