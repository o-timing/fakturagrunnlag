package com.example.otiming

@JvmInline
value class EventorParticipantId(val value: String)

data class Deltaker(
    val fornavn: String,
    val etternavn: String,
    val eventorId: EventorParticipantId?,
    val brikkenummer: Brikkenummer?,
    val leiebrikke: Leiebrikke?,
    val klubb: Klubb?
)

data class Klubb(
    val id: KlubbId,
    val navn: String
)

@JvmInline
value class KlubbId(val value: String)

data class Leiebrikke(
    val brikkenummer: Brikkenummer,
    val eier: String,
    val kortnavn: String?,
    val kommentar: String?
)

@JvmInline
value class Brikkenummer(val value: String)

