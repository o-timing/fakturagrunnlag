package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootTest
class CheckDbTests(@Autowired val jdbcTemplate: JdbcTemplate) {

    // TODO
    // - lag en tabell der man kan legge inn alle nydalens leiebrikker
    // - legg inn leiebrikker

    @Test
    fun testLagLeiebrikketabell(): Unit {
        val eTimingDbService = ETimingDbService(jdbcTemplate)
        val deltakere = eTimingDbService.hentAlleDeltakere()
        println(deltakere.joinToString("\n"))
        println(deltakere.size)
    }
}

