package otiming.fakturagrunnlag.excel

import org.apache.poi.xssf.usermodel.XSSFSheet

data class ExcelDefinitions(
    val sheetName: String,
    val definitions: List<ExcelDefinition>
) {
    fun render(sheet: XSSFSheet) {
        definitions.forEachIndexed { index, definition ->
            val row = sheet.createRow(index)
            row.createCell(0).setCellValue(definition.name)
            definition.value.insertIntoCell(row.createCell(1), RowNum(index), ColNum(1), null)
        }

        sheet.autoSizeColumn(0)
        sheet.autoSizeColumn(1)
    }

    fun lookupDefinition(name: String): ExcelReference? {
        return definitions.find { (key, excelValue) -> key == name }?.let {
            ExcelReference(
                sheetName,
                ColNum(1),
                RowNum(definitions.indexOf(it))
            )
        }
    }
}
