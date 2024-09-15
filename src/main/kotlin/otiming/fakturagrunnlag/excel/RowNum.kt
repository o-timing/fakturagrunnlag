package otiming.fakturagrunnlag.excel

data class RowNum(val value: Int) {
    fun render(): String {
        return (value + 1).toString()
    }

    fun add(i: RowNum): RowNum {
        return RowNum(value + i.value)
    }
}
