package otiming.fakturagrunnlag.excel

data class ExcelReference(
    val sheetName: String,
    val colNum: ColNum,
    val rowNum: RowNum
) {
    fun render(): String {
        return "$sheetName!${colNum.render()}${rowNum.render()}"
    }
}
