package com.example.otiming

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import com.example.otiming.OtimingDomain.KontigentRapportLinje
import com.example.otiming.OtimingDomain.LeiebrikkeRapportLinje
import com.example.otiming.OtimingDomain.BasisRapportLinje

@SpringBootTest
class OtimingFakturaRapportTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {
    val otimingFakturaRapport = OtimingFakturaRapport(jdbcTemplate)

    @Test
    fun fooTest() {
        val foo: Map<Int, List<BasisRapportLinje>> = otimingFakturaRapport.selectBasicReport()
        println(foo)
    }

    @Test
    fun kontigentRapport() {
        val foo: Map<Int, List<KontigentRapportLinje>> = otimingFakturaRapport.selectKontigentRapport()
        println(foo)
    }

    @Test
    fun leiebrikkeRapport() {
        val foo: Map<Int, List<LeiebrikkeRapportLinje>> = otimingFakturaRapport.selectLeiebrikkeRapport()
        println(foo)
    }


}

