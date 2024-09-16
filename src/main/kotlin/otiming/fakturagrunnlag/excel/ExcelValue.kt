package otiming.fakturagrunnlag.excel

import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFCellStyle
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator

sealed class ExcelValue {
    data class ExcelBool(val value: Boolean?) : ExcelValue()
    data class ExcelCurrency(val value: Double?, val style: XSSFCellStyle) : ExcelValue()
    data class ExcelDate(val value: String?, val style: XSSFCellStyle) : ExcelValue()
    data class ExcelDouble(val value: Double?) : ExcelValue()
    data class ExcelFormula(val style: XSSFCellStyle? = null, val value: (col: ColNum, row: RowNum) -> String) :
        ExcelValue()

    data class ExcelInt(val value: Int?) : ExcelValue()
    data class ExcelString(val value: String?) : ExcelValue()

    fun insertIntoCell(cell: XSSFCell, row: RowNum, col: ColNum, formulaEvaluator: XSSFFormulaEvaluator?) {
        when (this) {
            is ExcelBool -> value?.let { cell.setCellValue(it) }
            is ExcelCurrency ->
                value?.let {
                    cell.setCellValue(it)
                    cell.setCellStyle(style)
                }
            is ExcelDate ->
                value?.let {
                    cell.setCellValue(it)
                    cell.setCellStyle(style)
                }
            is ExcelDouble -> value?.let { cell.setCellValue(it) }
            is ExcelFormula ->
                value(col, row).let { s ->
                    cell.setCellFormula(s)
                    formulaEvaluator?.evaluateFormulaCell(cell)
                    style?.let { cell.setCellStyle(it) }
                }
            is ExcelInt -> value?.let { cell.setCellValue(it.toDouble()) }
            is ExcelString -> value?.let { cell.setCellValue(it) }
        }
    }
}



