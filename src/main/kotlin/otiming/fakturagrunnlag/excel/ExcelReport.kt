package otiming.fakturagrunnlag.excel

import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import otiming.fakturagrunnlag.OtimingDomain.BasisRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.KontigentRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.LeiebrikkeRapportLinje
import otiming.fakturagrunnlag.OtimingFakturaRapport
import java.io.File

class ExcelReport(val otimingFakturaRapport: OtimingFakturaRapport) {
    val LEIEBRIKKE_LEIE: Int = 50

    fun fakturagrunnlagExcel(databasenavn: String) {
        val fakturarapportlinjer = createFakturarapportlinjer()
        val workbook = createFakturaWorkbook(fakturarapportlinjer)

        val file = File("/Users/eirikm/projects/orientering/o-timing/fakturagrunnlag/$databasenavn.xlsx")
        workbook.write(file.outputStream())
    }

    fun createFakturarapportlinjer(): List<Fakturarapportlinje> {
        val basisrapportlinjer: List<BasisRapportLinje> = otimingFakturaRapport.selectBasicReport()
        val leiebrikkeRapport = otimingFakturaRapport.selectLeiebrikkeRapport()
        val kontigentRapport = otimingFakturaRapport.selectKontigentRapport()

        return basisrapportlinjer.map {
            val leiebrikkerapportlinje: LeiebrikkeRapportLinje? = leiebrikkeRapport.get(it.id)
            val kontigentRapportLinje: KontigentRapportLinje? = kontigentRapport.get(it.id)

            Fakturarapportlinje(
                id = it.id,
                klubb = it.klubb,
                distanse = it.distanse,
                dato = it.dato,
                startnr = it.startnr?.toDouble(),
                fornavn = it.fornavn,
                etternavn = it.etternavn,
                klasse = it.klasse,
                brikkenummer = leiebrikkerapportlinje?.etimingEcard,
                etimingEcardFee = leiebrikkerapportlinje?.etimingEcardFee,
                etimingEcard2 = leiebrikkerapportlinje?.ecard2,
                registrertLeiebrikkeNummer = leiebrikkerapportlinje?.leiebrikke_nummer,
                registrertLeiebrikkeEier = leiebrikkerapportlinje?.leiebrikke_eier,
                registrertLeiebrikkeKortnavn = leiebrikkerapportlinje?.leiebrikke_kortnavn,
                etimingKontigent1 = kontigentRapportLinje?.etimingEntryFee1,
                etimingKontigent2 = kontigentRapportLinje?.etimingEntryFee2,
                etimingKontigent3 = kontigentRapportLinje?.etimingEntryFee3,
                eventorKontigentNavn = kontigentRapportLinje?.eventorEntryFeeNames,
                eventorKontigentKalkulasjon = kontigentRapportLinje?.eventorEntryFeeCalculation,
                eventorKontigentSum = kontigentRapportLinje?.eventorEntryFeeSum,
            )
        }
    }

    private fun createFakturaWorkbook(input: List<Fakturarapportlinje>): XSSFWorkbook {
        val workbook = XSSFWorkbook()
        val workSheet = workbook.createSheet()

        val currencyStyle = workbook.createCellStyle()
        currencyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"))

        val dateStyle = workbook.createCellStyle()
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"))

        val formulaEvaluator: XSSFFormulaEvaluator =
            workbook.getCreationHelper().createFormulaEvaluator()

        val headerRow = workSheet.createRow(0)
        ExcelHeader2.entries.forEach {
            val cell =
                headerRow
                    .createCell(it.colIndex)
            cell.setCellValue(it.name)
        }

        var rownum = 0
        input.forEach {
            rownum++
            val row: XSSFRow = workSheet.createRow(rownum)
            it.insertIntoRow(row, currencyStyle, dateStyle, formulaEvaluator, LEIEBRIKKE_LEIE)
        }

        workSheet.createFreezePane(0, 1)

        workSheet.setAutoFilter(
            CellRangeAddress(
                /* firstRow = */ 0,
                /* lastRow = */ input.size,
                /* firstCol = */ 0,
                /* lastCol = */ ExcelHeader2.entries.map { it.colIndex }.max()
            )
        )

        return workbook
    }


}

data class Fakturarapportlinje(
    val id: Int,
    val klubb: String,
    val distanse: String,
    val dato: String,
    val startnr: Double?,
    val fornavn: String,
    val etternavn: String,
    val klasse: String,
    val brikkenummer: Int?,
    val etimingEcardFee: Boolean?,
    val etimingEcard2: Int?,
    val registrertLeiebrikkeNummer: Int?,
    val registrertLeiebrikkeEier: String?,
    val registrertLeiebrikkeKortnavn: String?,
    val etimingKontigent1: Double?,
    val etimingKontigent2: Double?,
    val etimingKontigent3: Double?,
    val eventorKontigentNavn: String?,
    val eventorKontigentKalkulasjon: String?,
    val eventorKontigentSum: Double?
) {
    val navn: String = "$fornavn $etternavn"

    val etimingLeiebrikke = (etimingEcard2 != null && etimingEcard2 != 0) || (etimingEcardFee ?: false)
    val otimingLeiebrikke = registrertLeiebrikkeNummer != null && registrertLeiebrikkeNummer != 0
    val utledetLeiebrikke: Boolean = etimingLeiebrikke || otimingLeiebrikke

    val etimingKontigentSum: Double =
        (etimingKontigent1 ?: 0.0) +
                (etimingKontigent2 ?: 0.0) +
                (etimingKontigent3 ?: 0.0)

    fun insertIntoRow(
        row: XSSFRow,
        currencyStyle: CellStyle,
        dateStyle: CellStyle,
        formulaEvaluator: XSSFFormulaEvaluator,
        leiebrikkeLeie: Int
    ): XSSFRow {
        row.createCell(ExcelHeader2.Klubb.colIndex).setCellValue(klubb)
        row.createCell(ExcelHeader2.Distanse.colIndex).setCellValue(distanse)
        val datoCell = row.createCell(ExcelHeader2.Dato.colIndex)
        datoCell.setCellValue(dato)
        datoCell.setCellStyle(dateStyle)
        startnr?.let {
            row.createCell(ExcelHeader2.Startnr.colIndex).setCellValue(it)
        }
        row.createCell(ExcelHeader2.Fornavn.colIndex).setCellValue(fornavn)
        row.createCell(ExcelHeader2.Etternavn.colIndex).setCellValue(etternavn)
        row.createCell(ExcelHeader2.Navn.colIndex).setCellValue(navn)
        row.createCell(ExcelHeader2.Klasse.colIndex).setCellValue(klasse)

        brikkenummer?.let {
            row.createCell(ExcelHeader2.Brikkenummer.colIndex).setCellValue(brikkenummer.toDouble())
        }

        val leiebrikkeCell = row.createCell(ExcelHeader2.Leiebrikke.colIndex)
        leiebrikkeCell.setCellStyle(currencyStyle)
        if (utledetLeiebrikke) leiebrikkeCell.setCellValue(leiebrikkeLeie.toDouble())

        val kontigentCell = row.createCell(ExcelHeader2.Kontigent.colIndex)
        kontigentCell.setCellValue(eventorKontigentSum ?: 0.0)
        kontigentCell.setCellStyle(currencyStyle)

        row.createCell(ExcelHeader2.KontigentNavn.colIndex).setCellValue(eventorKontigentNavn)

        row.sheet.setColumnHidden(ExcelHeader2.KontigentNavn.colIndex, true)

        val totalCell = row.createCell(ExcelHeader2.Total.colIndex)
        totalCell.setCellStyle(currencyStyle)
        val formula =
            "SUM(${ExcelHeader2.Leiebrikke.colName}${row.rowNum + 1}:${ExcelHeader2.Kontigent.colName}${row.rowNum + 1})"
        totalCell.setCellFormula(formula)
        formulaEvaluator.evaluateFormulaCell(totalCell)

        row.createCell(ExcelHeader2.Kommentar.colIndex)

        return row
    }
}

enum class ExcelHeader2(val colIndex: Int) {
    Klubb(0),
    Distanse(1),
    Dato(2),
    Startnr(3),
    Fornavn(4),
    Etternavn(5),
    Navn(6),
    Klasse(7),
    Brikkenummer(8),
    Leiebrikke(9),
    Kontigent(10),
    KontigentNavn(11),
    Total(12),
    Kommentar(13);

    val colName = ('A' + colIndex).toString()
}
