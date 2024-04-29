package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class KontigentRapportTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    @Test
    fun fooTest() {
        val foo: Map<Int, List<KontigentRapportLinje>> = selectKontigentRapport()
        println(foo)
    }

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

data class KontigentRapportLinje(
    val id: Int,
    val etimingEntryFee1: Double,
    val etimingEntryFee2: Double,
    val etimingEntryFee3: Double,
    val eventorEntryFeeNames: String?,
    val eventorEntryFeeCalculation: String?,
    val eventorEntryFeeSum: Double
)
