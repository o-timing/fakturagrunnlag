package otiming.fakturagrunnlag.excel

data class TableCell<T>(
    val title: String,
    val hidden: Boolean = false,
    val extract: (T) -> ExcelValue,
)
