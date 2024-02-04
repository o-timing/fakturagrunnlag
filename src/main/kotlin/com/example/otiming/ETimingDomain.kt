package com.example.otiming

@JvmInline
value class EventorParticipantId(val value: String)

data class Deltaker(
    val fornavn: String,
    val etternavn: String,
    val eventorId: EventorParticipantId?,
    val brikkenummer: Brikkenummer?,
    val leiebrikkeEier: String?
)

data class Leiebrikke(
    val brikkenummer: Brikkenummer,
    val eier: String,
    val kortnavn: String?,
    val kommentar: String?
)

@JvmInline
value class Brikkenummer(val value: String)

