package otiming.fakturagrunnlag.excel

import org.apache.poi.hssf.usermodel.HSSFDataFormat
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import otiming.fakturagrunnlag.OtimingDomain.BasisRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.KontigentRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.LeiebrikkeRapportLinje
import otiming.fakturagrunnlag.OtimingFakturaRapport
import otiming.fakturagrunnlag.excel.ExcelSelection.SameRowSelection
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelBool
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelCurrency
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelDate
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelDouble
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelFormula
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelInt
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelString
import otiming.fakturagrunnlag.leiebrikke.LeiebrikkeRepository
import otiming.fakturagrunnlag.leiebrikke.LeiebrikkeRow
import java.io.File

class ExcelReport(
    val otimingFakturaRapport: OtimingFakturaRapport,
    val leiebrikkeRepository: LeiebrikkeRepository
) {

    val LEIEBRIKKE_AVGIFT = ExcelDefinition("Leiebrikke avgift", ExcelInt(50))

    val definitions: ExcelDefinitions = ExcelDefinitions(
        "Variabler",
        listOf(
            LEIEBRIKKE_AVGIFT,
        )
    )

    fun fakturagrunnlagExcel(databasenavn: String) {
        val workbook: XSSFWorkbook = XSSFWorkbook()

        // variabler sheet
        val variablerSheet = createVariablerSheet(workbook)

        // oppsummering sheet
        val currencyStyle: XSSFCellStyle = workbook.createCellStyle()
        currencyStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0.00"))

        val dateStyle: XSSFCellStyle = workbook.createCellStyle()
        dateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("d-mmm-yy"))

        val formulaEvaluator: XSSFFormulaEvaluator =
            workbook.getCreationHelper().createFormulaEvaluator()

        val fakturarapportlinjer: List<Fakturarapportlinje> = createFakturarapportlinjer()

        val oppsummeringSheet =
            createOppsummeringSheet(workbook, fakturarapportlinjer, formulaEvaluator, dateStyle, currencyStyle)

        // leiebrikker sheet
        val leiebrikker: List<LeiebrikkeRow> = leiebrikkeRepository.getLeiebrikker()
        val leiebrikkerSheet = createLeiebrikkerSheet(workbook, leiebrikker)

        // rekkefølge
        workbook.setSheetOrder(oppsummeringSheet.sheetName, 0)
        workbook.setSheetOrder(variablerSheet.sheetName, 1)
        workbook.setSheetOrder(leiebrikkerSheet.sheetName, 2)
        workbook.setActiveSheet(0)
        workbook.setSelectedTab(0)

        // skriv til fil
        val file = File("/Users/eirikm/projects/orientering/o-timing/fakturagrunnlag/$databasenavn.xlsx")
        workbook.write(file.outputStream())
    }

    private fun createVariablerSheet(workbook: XSSFWorkbook): XSSFSheet {
        val sheet: XSSFSheet = workbook.createSheet(definitions.sheetName)

        definitions.render(sheet)

        return sheet
    }

    private fun createFakturarapportlinjer(): List<Fakturarapportlinje> {
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

    fun createOppsummeringSheet(
        workbook: XSSFWorkbook, linjer: List<Fakturarapportlinje>,
        formulaEvaluator: XSSFFormulaEvaluator,
        dateStyle: XSSFCellStyle, currencyStyle: XSSFCellStyle
    ): XSSFSheet {
        val sheet: XSSFSheet = workbook.createSheet("Oppsummering")

        val table: AutoFilterTable<Fakturarapportlinje> = AutoFilterTable(
            listOf(
                TableCell("Klubb") { ExcelString(it.klubb) },
                TableCell("Distanse") { ExcelString(it.distanse) },
                // TODO lag ekte dato av denne
                TableCell("Dato") { ExcelDate(it.dato, dateStyle) },
                TableCell("Startnr") { ExcelDouble(it.startnr) },
                TableCell("Fornavn", hidden = true) { ExcelString(it.fornavn) },
                TableCell("Etternavn", hidden = true) { ExcelString(it.etternavn) },
                TableCell("Navn") { ExcelString(it.fornavn + " " + it.etternavn) },
                TableCell("Klasse") { ExcelString(it.klasse) },
                TableCell("Brikkenummer") { ExcelInt(it.brikkenummer) },
                TableCell("etiming leiebrikke", hidden = true) { row ->
                    ExcelBool((row.etimingEcard2 != null && row.etimingEcard2 != 0) || (row.etimingEcardFee ?: false))
                },
                TableCell("otiming leiebrikke", hidden = true) { row ->
                    ExcelBool(row.registrertLeiebrikkeNummer != null && row.registrertLeiebrikkeNummer != 0)
                },
                TableCell("utledet leiebrikke", hidden = true) { row ->
                    ExcelFormula(currencyStyle) { colNum, rowNum ->
                        val selection = SameRowSelection(ColNum(-2), ColNum(-1)).render(colNum, rowNum)
                        "OR($selection)"
                    }
                },
                TableCell("leiebrikke") { row ->
                    ExcelFormula(currencyStyle) { colNum, rowNum ->
                        val predicateSelection = RelativeCellReference(ColNum(-1), RowNum(0)).render(colNum, rowNum)
                        val leiebrikkeAvgiftRef: ExcelReference = definitions.lookupDefinition(LEIEBRIKKE_AVGIFT.name)!!
                        "IF(${predicateSelection},${leiebrikkeAvgiftRef.render()},0)"
                    }
                },
                TableCell("etiming kontigent1", hidden = true) { row ->
                    ExcelCurrency(
                        row.etimingKontigent1,
                        currencyStyle
                    )
                },
                TableCell("eventor kontigentnavn", hidden = true) { row -> ExcelString(row.eventorKontigentNavn) },
                TableCell(
                    "eventor kontigentkalkulasjon",
                    hidden = true
                ) { row -> ExcelString(row.eventorKontigentKalkulasjon) },
                TableCell("eventor kontigentsum", hidden = true) { row -> ExcelDouble(row.eventorKontigentSum) },
                TableCell("kontigent") { row ->
                    ExcelFormula(currencyStyle) { colNum, rowNum ->
                        val eventorKontigentSumRef = RelativeCellReference(ColNum(-1), RowNum(0)).render(colNum, rowNum)
                        val etimingKontigent1Ref = RelativeCellReference(ColNum(-4), RowNum(0)).render(colNum, rowNum)
                        "IF($eventorKontigentSumRef=0,$etimingKontigent1Ref,$eventorKontigentSumRef)"
                    }
                },
                TableCell("Total") { row ->
                    ExcelFormula(currencyStyle) { colNum, rowNum ->
                        val leiebrikkeRef = RelativeCellReference(ColNum(-6), RowNum(0)).render(colNum, rowNum)
                        val kontigentRef = RelativeCellReference(ColNum(-1), RowNum(0)).render(colNum, rowNum)

                        "$leiebrikkeRef+$kontigentRef"
                    }
                }
            )
        )

        table.renderInSheet(sheet, linjer, formulaEvaluator)

        return sheet
    }

    fun createLeiebrikkerSheet(workbook: XSSFWorkbook, input: List<LeiebrikkeRow>): XSSFSheet {
        val sheet: XSSFSheet = workbook.createSheet("Leiebrikker")

        val table: AutoFilterTable<LeiebrikkeRow> = AutoFilterTable(listOf(
            TableCell("Kortnavn") { ExcelString(it.kortnavn) },
            TableCell("Brikkenummer") { ExcelString(it.brikkenummer.value) },
            TableCell("Eier") { ExcelString(it.eier) }
        ))

        table.renderInSheet(sheet, input)

        return sheet
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
)

