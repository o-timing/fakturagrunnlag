package otiming.fakturagrunnlag.excel

import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFSheet

data class AutoFilterTable<T>(
    val cells: List<TableCell<T>>
) {
    fun renderInSheet(sheet: XSSFSheet, values: List<T>, formulaEvaluator: XSSFFormulaEvaluator? = null) {
        renderHeader(sheet)
        renderBody(sheet, values, formulaEvaluator)
        sheet.setAutoFilter(
            CellRangeAddress(
                /* firstRow = */ 0,
                /* lastRow = */ values.size,
                /* firstCol = */ 0,
                /* lastCol = */ cells.size - 1
            )
        )

        cells.forEachIndexed { i, tableCell ->
            sheet.autoSizeColumn(i)
        }
    }

    private fun renderBody(sheet: XSSFSheet, values: List<T>, formulaEvaluator: XSSFFormulaEvaluator?) {
        values.forEachIndexed { y, value ->
            val row: XSSFRow = sheet.createRow(y + 1)
            val rowNum = RowNum(y + 1)
            cells.forEachIndexed { x, tableCell ->
                val colNum = ColNum(x)
                tableCell.extract(value).insertIntoCell(row.createCell(x), rowNum, colNum, formulaEvaluator)
            }
        }

        sheet.createFreezePane(0, 1)
    }

    private fun renderHeader(sheet: XSSFSheet) {
        val row: XSSFRow = sheet.createRow(0)
        cells.forEachIndexed { i, tableCell ->
            row.createCell(i).setCellValue(tableCell.title)
            sheet.setColumnHidden(i, tableCell.hidden)
        }
    }
}
