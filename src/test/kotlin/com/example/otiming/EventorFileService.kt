package com.example.otiming

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import java.nio.file.Files
import java.nio.file.Paths

class EventorFileService : AbstractEventorService() {

    override fun getEntries(eventId: EventId) =
        retrieveData<EntryList>("entries.xml")

    override fun getEntryFees(eventId: EventId) =
        retrieveData<EntryFeeList>("entryfees.xml")

    override fun getEventClasses(eventId: EventId) =
        retrieveData<EventClassList>("eventclasses.xml")

    private inline fun <reified T> retrieveData(fileName: String) =
        xmlStringAs<T>(slurpFile(fileName))

    private fun slurpFile(filename: String): String {
        val path = Paths.get(javaClass.classLoader.getResource(filename).toURI())
        return String(Files.readAllBytes(path))
    }
}