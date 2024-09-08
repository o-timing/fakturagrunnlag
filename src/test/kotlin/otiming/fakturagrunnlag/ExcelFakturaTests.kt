package otiming.fakturagrunnlag

import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@SpringBootTest
class ExcelFakturaTests() {

    data class FakturaRad(
        val klubb: String, // team.name
        val distanse: String, // arr.NAME
        val dato: LocalDate, // dato-delen av arr.firststart
        val startnr: Int, // name.startno
        val fornavn: String, // name.name
        val etternavn: String, // name.ename
        val klasse: String, // course.name
        val klassekontigent: Int, // class.entryfee koblet mot eventor sine entryfees
        val brikkeleie: Int, // name.ecardfee
        val etteranmelding: Int
    ) {
        fun navn(): String = "$fornavn $etternavn"

        fun insertIntoRow(row: XSSFRow, currencyStyle: CellStyle, dateStyle: CellStyle, formulaEvaluator: XSSFFormulaEvaluator): XSSFRow {
            val formatter = DataFormatter()
            row.createCell(ExcelHeader.Klubb.colIndex).setCellValue(klubb)
            row.createCell(ExcelHeader.Distanse.colIndex).setCellValue(distanse)
            val datoCell = row.createCell(ExcelHeader.Dato.colIndex)
            datoCell.setCellValue(dato)
            datoCell.setCellStyle(dateStyle)
            row.createCell(ExcelHeader.Startnr.colIndex).setCellValue(startnr.toDouble())
            row.createCell(ExcelHeader.Fornavn.colIndex).setCellValue(fornavn)
            row.createCell(ExcelHeader.Etternavn.colIndex).setCellValue(etternavn)
            row.createCell(ExcelHeader.Navn.colIndex).setCellValue(navn())
            row.createCell(ExcelHeader.Klasse.colIndex).setCellValue(klasse)

            val klassekontigentCell = row.createCell(ExcelHeader.Klassekontigent.colIndex)
            klassekontigentCell.setCellStyle(currencyStyle)
            if (klassekontigent > 0) klassekontigentCell.setCellValue(klassekontigent.toDouble())

            val brikkeleieCell = row.createCell(ExcelHeader.Brikkeleie.colIndex)
            brikkeleieCell.setCellStyle(currencyStyle)
            if (brikkeleie > 0) brikkeleieCell.setCellValue(brikkeleie.toDouble())

            val etteranmeldingCell = row.createCell(ExcelHeader.Etteranmelding.colIndex)
            etteranmeldingCell.setCellStyle(currencyStyle)
            if (etteranmelding > 0) etteranmeldingCell.setCellValue(etteranmelding.toDouble())

            val totalCell = row.createCell(ExcelHeader.Total.colIndex)
            totalCell.setCellStyle(currencyStyle)
            val formula =
                "SUM(${ExcelHeader.Klassekontigent.colName}${row.rowNum + 1}:${ExcelHeader.Etteranmelding.colName}${row.rowNum + 1})"
            totalCell.setCellFormula(formula)

            formulaEvaluator.evaluateFormulaCell(totalCell)

            return row
        }

    }

    enum class ExcelHeader(val colIndex: Int, val colName: String) {
        Klubb(0, "A"),
        Distanse(1, "B"),
        Dato(2, "C"),
        Startnr(3, "D"),
        Fornavn(4, "E"),
        Etternavn(5, "F"),
        Navn(6, "G"),
        Klasse(7, "H"),
        Klassekontigent(8, "I"),
        Brikkeleie(9, "J"),
        Etteranmelding(10, "K"),
        Total(11, "L")
    }

    val fakturarader = listOf(
        FakturaRad(
            klubb = "NSK",
            distanse = "Sprint",
            dato = LocalDate.parse("2024-02-25", DateTimeFormatter.ISO_DATE),
            startnr = 14,
            fornavn = "Jan",
            etternavn = "Banan",
            klasse = "H14",
            klassekontigent = 100,
            brikkeleie = 50,
            etteranmelding = 0
        ),
        FakturaRad(
            klubb = "Halden",
            distanse = "Sprint",
            dato = LocalDate.parse("2024-02-25", DateTimeFormatter.ISO_DATE),
            startnr = 18,
            fornavn = "Geir",
            etternavn = "Glom",
            klasse = "H14",
            klassekontigent = 100,
            brikkeleie = 0,
            etteranmelding = 10
        ),
        FakturaRad(
            klubb = "NSK",
            distanse = "Sprint",
            dato = LocalDate.parse("2024-02-25", DateTimeFormatter.ISO_DATE),
            startnr = 10,
            fornavn = "Jørn",
            etternavn = "Gluten",
            klasse = "H14",
            klassekontigent = 100,
            brikkeleie = 0,
            etteranmelding = 0
        ),
    )

    @Test
    fun testCreateExcelFile() {
        val workbook = createFakturaWorkbook(fakturarader + fakturarader + fakturarader + fakturarader + fakturarader)

        val file = File("/Users/eirikm/projects/orientering/o-timing/faktura2/test_output.xlsx")
        workbook.write(file.outputStream())
        workbook.close()
    }

    private fun createFakturaWorkbook(input: List<FakturaRad>): XSSFWorkbook {
        val workbook = XSSFWorkbook()
        val workSheet = workbook.createSheet()

        val currencyStyle = workbook.createCellStyle()
        currencyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"))

        val dateStyle = workbook.createCellStyle()
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"))

        val formulaEvaluator: XSSFFormulaEvaluator =
            workbook.getCreationHelper().createFormulaEvaluator()

        val headerRow = workSheet.createRow(0)
        ExcelHeader.entries.forEach {
            val cell =
                headerRow
                    .createCell(it.colIndex)
            cell.setCellValue(it.name)
        }

        var rownum = 0
        input.forEach {
            rownum++
            val row: XSSFRow = workSheet.createRow(rownum)
            it.insertIntoRow(row, currencyStyle, dateStyle, formulaEvaluator)
        }

        workSheet.createFreezePane(0, 1)

        workSheet.setAutoFilter(CellRangeAddress(
            /* firstRow = */ 0,
            /* lastRow = */ input.size,
            /* firstCol = */ 0,
            /* lastCol = */ ExcelHeader.entries.map { it.colIndex }.max()
        )
        )

        return workbook
    }

}

