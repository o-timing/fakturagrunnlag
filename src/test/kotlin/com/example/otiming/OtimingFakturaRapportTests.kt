package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import com.example.otiming.OtimingDomain.KontigentRapportLinje
import com.example.otiming.OtimingDomain.LeiebrikkeRapportLinje
import com.example.otiming.OtimingDomain.BasisRapportLinje
import java.time.LocalDate

@SpringBootTest
class OtimingFakturaRapportTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {
    val otimingFakturaRapport = OtimingFakturaRapport(jdbcTemplate)

    @Test
    fun basisRapport() {
        val foo: List<BasisRapportLinje> = otimingFakturaRapport.selectBasicReport()
        println(foo)
    }

    @Test
    fun kontigentRapport() {
        val foo: Map<Int, KontigentRapportLinje> = otimingFakturaRapport.selectKontigentRapport()
        println(foo)
    }

    @Test
    fun leiebrikkeRapport() {
        val foo: Map<Int, LeiebrikkeRapportLinje> = otimingFakturaRapport.selectLeiebrikkeRapport()
        println(foo)
    }


    @Test
    fun fakturarapport() {
        val basisrapportlinjer: List<BasisRapportLinje> = otimingFakturaRapport.selectBasicReport()
        val leiebrikkeRapport = otimingFakturaRapport.selectLeiebrikkeRapport()
        val kontigentRapport = otimingFakturaRapport.selectKontigentRapport()

        basisrapportlinjer.map {
            val leiebrikkerapportlinje: LeiebrikkeRapportLinje? = leiebrikkeRapport.get(it.id)
            val kontigentRapportLinje: KontigentRapportLinje? = kontigentRapport.get(it.id)

            Fakturarapportlinje(
                id = it.id,
                klubb = it.klubb,
                distanse = it.distanse,
                dato = it.dato,
                startnr = it.startnr,
                fornavn = it.fornavn,
                etternavn = it.etternavn,
                klasse = it.klasse,
                etimingEcardFee = leiebrikkerapportlinje?.etimingEcardFee,
                etimingEcard2 = leiebrikkerapportlinje?.ecard2,
                registrertLeiebrikkeNummer = leiebrikkerapportlinje?.leiebrikke_nummer,
                registrertLeiebrikkeEier = leiebrikkerapportlinje?.leiebrikke_eier,
                registrertLeiebrikkeKortnavn = leiebrikkerapportlinje?.leiebrikke_kortnavn,
                etimingKontigent1 = kontigentRapportLinje?.etimingEntryFee1,
                etimingKontigent2 = kontigentRapportLinje?.etimingEntryFee2,
                etimingKontigent3 = kontigentRapportLinje?.etimingEntryFee3,
                eventorKontigentNavn = kontigentRapportLinje?.eventorEntryFeeNames,
                eventorKontigentKalkulasjon = kontigentRapportLinje?.eventorEntryFeeCalculation,
                eventorKontigentSum = kontigentRapportLinje?.eventorEntryFeeSum,
            )
        }.forEach { println(it) }
    }

}

data class Fakturarapportlinje(
    val id: Int,
    val klubb: String,
    val distanse: String,
    val dato: String,
    val startnr: String?,
    val fornavn: String,
    val etternavn: String,
    val klasse: String,
    val etimingEcardFee: Boolean?,
    val etimingEcard2: Int?,
    val registrertLeiebrikkeNummer: Int?,
    val registrertLeiebrikkeEier: String?,
    val registrertLeiebrikkeKortnavn: String?,
    val etimingKontigent1: Double?,
    val etimingKontigent2: Double?,
    val etimingKontigent3: Double?,
    val eventorKontigentNavn: String?,
    val eventorKontigentKalkulasjon: String?,
    val eventorKontigentSum: Double?
) {
    val navn: String = fornavn + " " + etternavn
    val utledetLeiebrikke: Boolean =
        etimingEcardFee ?: false || etimingEcard2 != null || registrertLeiebrikkeNummer != null

    val etimingKontigentSum: Double =
        (etimingKontigent1 ?: 0.0) +
                (etimingKontigent2 ?: 0.0) +
                (etimingKontigent3 ?: 0.0)
}