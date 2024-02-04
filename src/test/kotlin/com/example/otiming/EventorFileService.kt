package com.example.otiming

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import java.nio.file.Files
import java.nio.file.Paths

class EventorFileService : AbstractEventorService() {

    override fun getEntries(eventId: Int): EntryList? {
        return xmlStringAs<EntryList>(slurpFile("entries.xml"))
    }

    override fun getEntryFees(eventId: Int): EntryFeeList? {
        return xmlStringAs<EntryFeeList>(slurpFile("entryfees.xml"))
    }

    override fun getEventClasses(eventId: Int): EventClassList? {
        return xmlStringAs<EventClassList>(slurpFile("eventclasses.xml"))
    }

    private fun slurpFile(filename: String): String {
        val path = Paths.get(javaClass.classLoader.getResource(filename).toURI())
        return String(Files.readAllBytes(path))
    }
}