package com.example.otiming

import generated.EventClass
import generated.EventClassList
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

@SpringBootTest
class PopulateEventorTablesTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: EventorConfig
) {

    @Test
    fun populateOtimingEventorEventClasses() {
        val eventorService = EventorServiceImpl(config)

        val eventId = 19449
        val xmlString = eventorService.getEventClassesRaw(eventId)
        println(xmlString)

        val eventClassList: EventClassList? = xmlString?.let {
            eventorService.xmlStringAs<EventClassList>(it)
        }

        eventClassList?.let {
            it.eventClass.forEach {
              ec: EventClass -> ec
            }
        }
    }
}

