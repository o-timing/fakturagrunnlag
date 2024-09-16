package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.OtimingDomain.BasisRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.KontigentRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.LeiebrikkeRapportLinje
import otiming.fakturagrunnlag.excel.ExcelReport
import otiming.fakturagrunnlag.leiebrikke.LeiebrikkeRepository

@SpringBootTest
class OtimingFakturaRapportTests(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {
    val otimingFakturaRapport = OtimingFakturaRapport(jdbcTemplate)
    val leiebrikkeRepository = LeiebrikkeRepository(jdbcTemplate)
    val excelReport = ExcelReport(otimingFakturaRapport, leiebrikkeRepository)


    @Test
    fun basisRapport() {
        val foo: List<BasisRapportLinje> = otimingFakturaRapport.selectBasicReport()
        println(foo)
    }

    @Test
    fun kontigentRapport() {
        val foo: Map<Int, KontigentRapportLinje> = otimingFakturaRapport.selectKontigentRapport()
        println(foo)
    }

    @Test
    fun leiebrikkeRapport() {
        val foo: Map<Int, LeiebrikkeRapportLinje> = otimingFakturaRapport.selectLeiebrikkeRapport()
        println(foo)
    }


    @Test
    fun fakturarapport() {
        val fakturarapportlinjer = excelReport.createFakturarapportlinjer()

        fakturarapportlinjer.forEach {
            if (it.eventorKontigentNavn == null) {
                println(it)
            }
        }
    }
}



