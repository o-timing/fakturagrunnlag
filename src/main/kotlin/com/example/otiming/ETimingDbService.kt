package com.example.otiming

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.internal.toImmutableList
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

class ETimingDbService(val jdbcTemplate: JdbcTemplate) {

    fun findEventIds(): List<Int> {
        logger.info { "getting eventId from database" }
        val rowMapper: RowMapper<Int> = RowMapper<Int> { resultSet: ResultSet, _: Int ->
            resultSet.getInt("id")
        }


        val results = jdbcTemplate.query(
            """
                select id
                from day
            """.trimMargin(),
            rowMapper
        )

        logger.info { "Got eventIds from database: ${results.joinToString(", ")}" }

        return results.toImmutableList()
    }

    fun hentAlleDeltakere(): List<Deltaker> =
        jdbcTemplate.query(
            """
                select name, ename, kid, ecard, otiming_leiebrikker.eier as leiebrikkeeier
                from name
                    left outer join otiming_leiebrikker on (name.ecard::text = otiming_leiebrikker.brikkenummer)
                where name != 'Ledig'
                  and ename != 'Startnr'
  		    """.trimIndent()
        ) { response, _ ->
            Deltaker(
                fornavn = response.getString("name").trim(),
                etternavn = response.getString("ename").trim(),
                eventorId =
                response.getString("kid")?.trim()?.let { EventorParticipantId(it) },
                brikkenummer = Brikkenummer(response.getString("ecard").trim()),
                leiebrikkeEier = response.getString("leiebrikkeeier")
            )
        }.toImmutableList()

    fun getLeiebrikkeMap(): Map<String, Leiebrikke> {
        val leiebrikker = jdbcTemplate.query(
            """
            select brikkenummer, eier, kortnavn, kommentar
            from otiming_leiebrikker
            """.trimIndent()
        ) { res, _ ->
            val brikkenummer = res.getString("brikkenummer")
            Pair(
                brikkenummer,
                Leiebrikke(
                    brikkenummer = Brikkenummer(brikkenummer),
                    eier = res.getString("eier"),
                    kortnavn = res.getString("kortnavn"),
                    kommentar = res.getString("kommentar")
                )
            )
        }

        return leiebrikker.toMap()
    }

}
