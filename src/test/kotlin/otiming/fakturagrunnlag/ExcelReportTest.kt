package otiming.fakturagrunnlag

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.excel.ExcelReport
import otiming.fakturagrunnlag.leiebrikke.LeiebrikkeRepository

@SpringBootTest
class ExcelReportTest(
    @Autowired jdbcTemplate: JdbcTemplate
) {

    val otimingFakturaRapport = OtimingFakturaRapport(jdbcTemplate)
    val leiebrikkeRepository = LeiebrikkeRepository(jdbcTemplate)
    val excelReport = ExcelReport(otimingFakturaRapport, leiebrikkeRepository)

    @Test
    fun testExcelReport() {
        excelReport.fakturagrunnlagExcel("fakturagrunnlag_fra_test")
    }
}

