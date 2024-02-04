package com.example.otiming

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "o-timing.eventor")
data class EventorConfig(
    val apiKey: String
)

class EventorServiceImpl(val config: EventorConfig) : AbstractEventorService() {
    val client = OkHttpClient()

    override fun getEntries(eventId: Int): EntryList? {
        val request = Request.Builder()
            .url("https://eventor.orientering.no/api/entries?eventIds=$eventId&includeEntryFees=true")
            .addHeader("ApiKey", config.apiKey)
            .build()

        val xmlString: String? = client.newCall(request).execute().use { response ->
            response.body?.string()
        }

        return xmlString?.let { xmlStringAs<EntryList>(it) }
    }


    override fun getEntryFees(eventId: Int): EntryFeeList? {
        val request = Request.Builder()
            .url("https://eventor.orientering.no/api/entryfees/events/$eventId")
            .addHeader("ApiKey", config.apiKey)
            .build()

        val xmlString: String? = client.newCall(request).execute().use { response ->
            response.body?.string()
        }

        return xmlString?.let { xmlStringAs<EntryFeeList>(it) }
    }

    override fun getEventClasses(eventId: Int): EventClassList? {
        val request = Request.Builder()
            .url("https://eventor.orientering.no/api/eventclasses?eventId=$eventId&includeEntryFees=true")
            .addHeader("ApiKey", config.apiKey)
            .build()

        val xmlString: String? = client.newCall(request).execute().use { response ->
            response.body?.string()
        }

        return xmlString?.let { xmlStringAs<EventClassList>(it) }
    }

}