package otiming.fakturagrunnlag

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

@JvmInline
value class EntryId(val value: Int)

fun generated.EntryId.toInternalId(): EntryId {
    return EntryId(content.toInt())
}

@JvmInline
value class EntryFeeId(val value: Int)

fun generated.EntryFeeId.toInternalId(): EntryFeeId {
    return EntryFeeId(content.toInt())
}

@JvmInline
value class EventId(val value: Int)

fun generated.EventId.toInternalId(): EventId {
    return EventId(content.toInt())
}

@JvmInline
value class PersonId(val value: Int)

fun generated.PersonId.toInternalId(): PersonId {
    return PersonId(content.toInt())
}

@JvmInline
value class EventClassId(val value: Int)

fun generated.EventClassId.toInternalId(): EventClassId {
    return EventClassId(content.toInt())
}