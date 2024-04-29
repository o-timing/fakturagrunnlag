package com.example.otiming

object OtimingDomain {

    data class BasisRapportLinje(
        val id: Int,
        val klubb: String,
        val distanse: String,
        val dato: String,
        val startnr: String?,
        val fornavn: String,
        val etternavn: String,
        val klasse: String
    )

    data class KontigentRapportLinje(
        val id: Int,
        val etimingEntryFee1: Double,
        val etimingEntryFee2: Double,
        val etimingEntryFee3: Double,
        val eventorEntryFeeNames: String?,
        val eventorEntryFeeCalculation: String?,
        val eventorEntryFeeSum: Double
    )

    data class LeiebrikkeRapportLinje(
        val id: Int,
        val ecard1: Int,
        val ecard2: Int?,
        val etimingEcard: Int,
        val etimingEcardFee: Boolean,
        val leiebrikke_nummer: Int?,
        val leiebrikke_eier: String?,
        val leiebrikke_kortnavn: String?,
    )
}