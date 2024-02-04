package com.example.otiming

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import jakarta.xml.bind.JAXBContext
import java.io.StringReader

abstract class AbstractEventorService {
    abstract fun getEntries(eventId: Int): EntryList?
    abstract fun getEntryFees(eventId: Int): EntryFeeList?
    abstract fun getEventClasses(eventId: Int): EventClassList?

    inline fun <reified T> xmlStringAs(xmlString: String): T {
        val context = JAXBContext.newInstance(T::class.java)
        val stringReader = StringReader(xmlString)
        val unmarshaller = context.createUnmarshaller()
        return unmarshaller.unmarshal(stringReader) as T
    }

}