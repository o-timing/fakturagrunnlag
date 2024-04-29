package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import com.example.otiming.OtimingDomain.KontigentRapportLinje

@SpringBootTest
class KontigentRapportTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {
    val otimingFakturaRapport = OtimingFakturaRapport(jdbcTemplate)

    @Test
    fun fooTest() {
        val foo: Map<Int, List<KontigentRapportLinje>> = otimingFakturaRapport.selectKontigentRapport()
        println(foo)
    }


}

