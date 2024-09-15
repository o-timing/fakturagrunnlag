package otiming.fakturagrunnlag

import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.excel.AutoFilterTable
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelString
import otiming.fakturagrunnlag.excel.TableCell
import otiming.fakturagrunnlag.leiebrikke.LeiebrikkeRepository
import otiming.fakturagrunnlag.leiebrikke.LeiebrikkeRow
import java.io.File

@SpringBootTest
class LeiebrikkeSheetTest(
    @Autowired val jdbcTemplate: JdbcTemplate,
) {

    val leiebrikkeRepository = LeiebrikkeRepository(jdbcTemplate)

    @Test
    fun leiebrikkeRapport() {
        val leiebrikker: List<LeiebrikkeRow> = leiebrikkeRepository.getLeiebrikker()

        val workbook = createLeiebrikkeWorkbook(leiebrikker)

        val file = File("/Users/eirikm/projects/orientering/o-timing/fakturagrunnlag/leiebrikker.xlsx")
        workbook.write(file.outputStream())
    }

    private fun createLeiebrikkeWorkbook(input: List<LeiebrikkeRow>): XSSFWorkbook {
        val workbook = XSSFWorkbook()
        val workSheet: XSSFSheet = workbook.createSheet("Leiebrikker")

        val table: AutoFilterTable<LeiebrikkeRow> = AutoFilterTable(listOf(
            TableCell("Kortnavn") { ExcelString(it.kortnavn) },
            TableCell("Brikkenummer") { ExcelString(it.brikkenummer.value) },
            TableCell("Eier") { ExcelString(it.eier) }
        ))

        table.renderInSheet(workSheet, input)

        return workbook
    }
}

