package com.example.otiming

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.boot.context.properties.ConfigurationProperties

class EventorServiceImpl(val config: EventorConfig) : AbstractEventorService() {
    val client = OkHttpClient()

    override fun getEntries(eventId: EventId): EntryList? {
        return getEntriesRaw(eventId)?.let { xmlStringAs<EntryList>(it) }
    }

    fun getEntriesRaw(eventId: EventId): String? {
        return fetchStringFromEventorEndpoint("https://eventor.orientering.no/api/entries?eventIds=${eventId.value}&includeEntryFees=true&includePersonElement=true")
    }

    override fun getEntryFees(eventId: EventId): EntryFeeList? {
        return getEntryFeesRaw(eventId)?.let { xmlStringAs<EntryFeeList>(it) }
    }

    fun getEntryFeesRaw(eventId: EventId): String? {
        return fetchStringFromEventorEndpoint("https://eventor.orientering.no/api/entryfees/events/${eventId.value}")
    }

    override fun getEventClasses(eventId: EventId): EventClassList? {
        return getEventClassesRaw(eventId)?.let { xmlStringAs<EventClassList>(it) }
    }

    fun getEventClassesRaw(eventId: EventId): String? {
        return fetchStringFromEventorEndpoint("https://eventor.orientering.no/api/eventclasses?eventId=${eventId.value}&includeEntryFees=true")
    }

    private fun fetchStringFromEventorEndpoint(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .addHeader("ApiKey", config.apiKey)
            .build()

        return client.newCall(request).execute().use { response ->
            response.body?.string()
        }
    }
}