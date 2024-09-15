package otiming.fakturagrunnlag.excel

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator

sealed class ExcelValue {
    data class ExcelString(val value: String?) : ExcelValue()
    data class ExcelInt(val value: Int?) : ExcelValue()
    data class ExcelDouble(val value: Double?) : ExcelValue()
    data class ExcelBool(val value: Boolean?) : ExcelValue()
    data class ExcelFormula(val value: (col: ColNum, row: RowNum) -> String) : ExcelValue()

    fun insertIntoCell(cell: XSSFCell, row: RowNum, col: ColNum, formulaEvaluator: XSSFFormulaEvaluator?) {
        when (this) {
            is ExcelString ->
                value?.let { cell.setCellValue(it) }

            is ExcelInt ->
                value?.let { cell.setCellValue(it.toDouble()) }

            is ExcelDouble ->
                value?.let { cell.setCellValue(it) }

            is ExcelBool ->
                value?.let { cell.setCellValue(it) }

            is ExcelFormula ->
                value(col, row).let { s ->
                    cell.setCellFormula(s)
                    formulaEvaluator?.evaluateFormulaCell(cell)
                }
        }
    }
}



