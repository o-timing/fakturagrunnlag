package otiming.fakturagrunnlag.excel

sealed class ExcelSelection {
    data class SameRowSelection(val from: ColNum, val to: ColNum) : ExcelSelection()

    fun render(col: ColNum, row: RowNum): String {
        return when (this) {
            is SameRowSelection -> {
                val fromCol = col.add(from)
                val from = "${fromCol.render()}${row.render()}"
                val toCol = col.add(to)
                val to = "${toCol.render()}${row.render()}"
                "$from:$to"
            }
        }
    }
}
