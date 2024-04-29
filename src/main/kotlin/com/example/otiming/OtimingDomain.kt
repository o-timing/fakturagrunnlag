package com.example.otiming

object OtimingDomain {

    data class KontigentRapportLinje(
        val id: Int,
        val etimingEntryFee1: Double,
        val etimingEntryFee2: Double,
        val etimingEntryFee3: Double,
        val eventorEntryFeeNames: String?,
        val eventorEntryFeeCalculation: String?,
        val eventorEntryFeeSum: Double
    )

}