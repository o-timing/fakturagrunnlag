package com.example.otiming

import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.internal.toImmutableList
import org.springframework.jdbc.core.JdbcTemplate
import java.sql.ResultSet

private val logger = KotlinLogging.logger {}

class ETimingDbService(val jdbcTemplate: JdbcTemplate) {

    fun findEventIds(): List<Int> {
        logger.info { "getting eventId from database" }

        val results = jdbcTemplate.query(
            """
                select id
                from day
            """.trimIndent()
        ) { resultSet: ResultSet, _: Int ->
            resultSet.getInt("id")
        }

        logger.info { "Got eventIds from database: ${results.joinToString(", ")}" }

        return results.toImmutableList()
    }

    fun hentAlleDeltakere(): List<Deltaker> =
        jdbcTemplate.query(
            """
                select name.name, ename, name.kid, ecard,
                    otiming_leiebrikker.brikkenummer as leiebrikkenummer,
                    otiming_leiebrikker.eier as leiebrikkeeier,
                    otiming_leiebrikker.kortnavn as leiebrikkekortnavn,
                    otiming_leiebrikker.kommentar as leiebrikkekommentar,
                    team.code as klubb_id, team.name as klubb_navn
                from name
                         left outer join otiming_leiebrikker on (CONVERT(VARCHAR(10), name.ecard) = otiming_leiebrikker.brikkenummer)
                         left outer join team on (name.team = team.code)
                where name.name != 'Ledig'
                  and ename != 'Startnr'
  		    """.trimIndent()
        ) { response, _ ->
            Deltaker(
                fornavn = response.getString("name").trim(),
                etternavn = response.getString("ename").trim(),
                eventorId = response.getString("kid")?.trim()?.let { EventorParticipantId(it) },
                brikkenummer = response.getString("ecard")?.trim()?.let { Brikkenummer(it) },
                leiebrikke = response.getString("leiebrikkenummer")?.let { leiebrikkenummer ->
                    Leiebrikke(
                        brikkenummer = Brikkenummer(leiebrikkenummer),
                        eier = response.getString("leiebrikkeeier"),
                        kortnavn = response.getString("leiebrikkekortnavn"),
                        kommentar = response.getString("leiebrikkekommentar")
                    )
                },
                klubb = response.getString("klubb_id")?.let {
                    Klubb(
                        id = KlubbId(it),
                        navn = response.getString("klubb_navn")
                    )
                }
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
