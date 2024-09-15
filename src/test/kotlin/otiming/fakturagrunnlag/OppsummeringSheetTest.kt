package otiming.fakturagrunnlag

import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import otiming.fakturagrunnlag.OtimingDomain.BasisRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.KontigentRapportLinje
import otiming.fakturagrunnlag.OtimingDomain.LeiebrikkeRapportLinje
import otiming.fakturagrunnlag.excel.AutoFilterTable
import otiming.fakturagrunnlag.excel.ColNum
import otiming.fakturagrunnlag.excel.ExcelDefinition
import otiming.fakturagrunnlag.excel.ExcelDefinitions
import otiming.fakturagrunnlag.excel.ExcelReference
import otiming.fakturagrunnlag.excel.ExcelSelection.SameRowSelection
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelBool
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelDouble
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelFormula
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelInt
import otiming.fakturagrunnlag.excel.ExcelValue.ExcelString
import otiming.fakturagrunnlag.excel.RelativeCellReference
import otiming.fakturagrunnlag.excel.RowNum
import otiming.fakturagrunnlag.excel.TableCell
import java.io.File

@SpringBootTest
class OppsummeringSheetTest(
    @Autowired jdbcTemplate: JdbcTemplate
) {

    val otimingFakturaRapport = OtimingFakturaRapport(jdbcTemplate)

    val LEIEBRIKKE_AVGIFT = ExcelDefinition("Leiebrikke avgift", ExcelInt(50))

    val definitions: ExcelDefinitions = ExcelDefinitions(
        "Variabler",
        listOf(
            LEIEBRIKKE_AVGIFT,
        )
    )

    @Test
    fun testOppsummeringSheet() {
        val fakturarapportlinjer: List<Fakturarapportlinje> = createFakturarapportlinjer()

        val workbook = createOppsummeringWorkbook(fakturarapportlinjer)

        val file = File("/Users/eirikm/projects/orientering/o-timing/fakturagrunnlag/oppsummering.xlsx")
        workbook.write(file.outputStream())
    }

    fun createOppsummeringWorkbook(linjer: List<Fakturarapportlinje>): XSSFWorkbook {
        val workbook = XSSFWorkbook()
        val workSheet: XSSFSheet = workbook.createSheet("Oppsummering")

        createVariablerWorksheet(workbook)

        val table: AutoFilterTable<Fakturarapportlinje> = AutoFilterTable(
            listOf(
                TableCell("Klubb") { ExcelString(it.klubb) },
                TableCell("Distanse") { ExcelString(it.distanse) },
                // TODO lag ekte dato av denne
                TableCell("Dato") { ExcelString(it.dato) },
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
                    ExcelFormula { colNum, rowNum ->
                        val selection = SameRowSelection(ColNum(-2), ColNum(-1)).render(colNum, rowNum)
                        "OR($selection)"
                    }
                },
                TableCell("leiebrikke") { row ->
                    ExcelFormula { colNum, rowNum ->
                        val predicateSelection = RelativeCellReference(ColNum(-1), RowNum(0)).render(colNum, rowNum)
                        val leiebrikkeAvgiftRef: ExcelReference = definitions.lookupDefinition(LEIEBRIKKE_AVGIFT.name)!!
                        "IF(${predicateSelection},${leiebrikkeAvgiftRef.render()},0)"
                    }
                },
                TableCell("etiming kontigent1", hidden = true) { row -> ExcelDouble(row.etimingKontigent1) },
                TableCell("eventor kontigentnavn", hidden = true) { row -> ExcelString(row.eventorKontigentNavn) },
                TableCell(
                    "eventor kontigentkalkulasjon",
                    hidden = true
                ) { row -> ExcelString(row.eventorKontigentKalkulasjon) },
                TableCell("eventor kontigentsum", hidden = true) { row -> ExcelDouble(row.eventorKontigentSum) },
                TableCell("kontigent") { row ->
                    ExcelFormula { colNum, rowNum ->
                        val eventorKontigentSumRef = RelativeCellReference(ColNum(-1), RowNum(0)).render(colNum, rowNum)
                        val etimingKontigent1Ref = RelativeCellReference(ColNum(-4), RowNum(0)).render(colNum, rowNum)
                        "IF($eventorKontigentSumRef=0,$etimingKontigent1Ref,$eventorKontigentSumRef)"
                    }
                },
                TableCell("Total sum") { row ->
                    ExcelFormula { colNum, rowNum ->
                        val leiebrikkeRef = RelativeCellReference(ColNum(-6), RowNum(0)).render(colNum, rowNum)
                        val kontigentRef = RelativeCellReference(ColNum(-1), RowNum(0)).render(colNum, rowNum)

                        "$leiebrikkeRef+$kontigentRef"
                    }
                }
            )
        )

        val formulaEvaluator: XSSFFormulaEvaluator =
            workbook.getCreationHelper().createFormulaEvaluator()


        table.renderInSheet(workSheet, linjer, formulaEvaluator)

        return workbook
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

    private fun createVariablerWorksheet(workbook: XSSFWorkbook): XSSFWorkbook {
        val workSheet: XSSFSheet = workbook.createSheet(definitions.sheetName)

        definitions.render(workSheet)

        return workbook
    }

}

