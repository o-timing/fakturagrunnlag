package otiming.fakturagrunnlag.leiebrikke

import otiming.fakturagrunnlag.Brikkenummer

data class LeiebrikkeCsvRow(
    val kortnavn: String?,
    val brikkenummer: Brikkenummer,
    val eier: String?,
    val kommentar: String?
)
