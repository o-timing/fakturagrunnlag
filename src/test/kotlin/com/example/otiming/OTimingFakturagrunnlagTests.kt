package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class OTimingFakturagrunnlagTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
    @Autowired val config: EventorConfig
) {

    // TODO
    // - lag en tabell der man kan legge inn alle nydalens leiebrikker
    // - legg inn leiebrikker

    @Test
    fun contextLoads() {
        val eventorService = EventorFileService()

        println(eventorService.getEntries(EventId(18000)))
        println(eventorService.getEntryFees(EventId(18000)))
        println(eventorService.getEventClasses(EventId(18000)))
    }

    @Test
    fun testHentDeltakere(): Unit {
        val eTimingDbService = ETimingDbService(jdbcTemplate)
        val deltakere = eTimingDbService.hentAlleDeltakere()
        println(deltakere.joinToString("\n"))
        println(deltakere.size)
    }


    @Test
    fun testHentUtAlleLeiebrikker() {
        val eTimingDbService = ETimingDbService(jdbcTemplate)

        val leiebrikkeMap = eTimingDbService.getLeiebrikkeMap()

        println(leiebrikkeMap.size)
    }


}

