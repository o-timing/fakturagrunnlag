package com.example.otiming

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import jakarta.xml.bind.JAXBContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.boot.context.properties.ConfigurationProperties
import java.io.StringReader

@ConfigurationProperties(prefix = "o-timing.eventor")
data class EventorConfig(
    val apiKey: String
)

class EventorService(val config: EventorConfig) {
    val client = OkHttpClient()

    fun fetchEntries(eventId: Int): EntryList? {
        val request = Request.Builder()
            .url("https://eventor.orientering.no/api/entries?eventIds=$eventId&includeEntryFees=true")
            .addHeader("ApiKey", config.apiKey)
            .build()

        val xmlString: String? = client.newCall(request).execute().use { response ->
            response.body?.string()
        }

        val context = JAXBContext.newInstance(EntryList::class.java)
        val xml = xmlString?.let {
            val stringReader = StringReader(it)
            val unmarshaller = context.createUnmarshaller()

            unmarshaller.unmarshal(stringReader) as EntryList
        }
        return xml
    }

    fun getEntryFeeList(eventId: Int): EntryFeeList? {
        val request = Request.Builder()
            .url("https://eventor.orientering.no/api/entryfees/events/$eventId")
            .addHeader("ApiKey", config.apiKey)
            .build()

        val xmlString: String? = client.newCall(request).execute().use { response ->
            response.body?.string()
        }

        val entryFeeList = xmlString?.let { asEntryFeeList(it) }

        return entryFeeList
    }

    fun asEntryFeeList(xmlString: String): EntryFeeList {
        val context = JAXBContext.newInstance(EntryFeeList::class.java)
        val stringReader = StringReader(xmlString)
        val unmarshaller = context.createUnmarshaller()
        return unmarshaller.unmarshal(stringReader) as EntryFeeList
    }

    fun getEventClasses(eventId: Int): EventClassList? {
        val request = Request.Builder()
            .url("https://eventor.orientering.no/api/eventclasses?eventId=$eventId&includeEntryFees=true")
            .addHeader("ApiKey", config.apiKey)
            .build()

        val xmlString: String? = client.newCall(request).execute().use { response ->
            response.body?.string()
        }

        val context = JAXBContext.newInstance(EventClassList::class.java)
        val xml: EventClassList? = xmlString?.let {
            val stringReader = StringReader(it)
            val unmarshaller = context.createUnmarshaller()

            unmarshaller.unmarshal(stringReader) as EventClassList
        }
        return xml
    }

}