package otiming.fakturagrunnlag.excel

data class RelativeCellReference(
    val colNum: ColNum,
    val rowNum: RowNum
) {
    fun render(col: ColNum, row: RowNum): String {
        return "${col.add(colNum).render()}${row.add(rowNum).render()}"
    }

}