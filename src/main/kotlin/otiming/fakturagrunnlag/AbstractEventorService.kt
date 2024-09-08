package otiming.fakturagrunnlag

import generated.EntryFeeList
import generated.EntryList
import generated.EventClassList
import jakarta.xml.bind.JAXBContext
import java.io.StringReader

abstract class AbstractEventorService {
    abstract fun getEntries(eventId: EventId): EntryList?
    abstract fun getEntryFees(eventId: EventId): EntryFeeList?
    abstract fun getEventClasses(eventId: EventId): EventClassList?

    inline fun <reified T> xmlStringAs(xmlString: String): T {
        val context = JAXBContext.newInstance(T::class.java)
        val stringReader = StringReader(xmlString)
        val unmarshaller = context.createUnmarshaller()
        return unmarshaller.unmarshal(stringReader) as T
    }

}