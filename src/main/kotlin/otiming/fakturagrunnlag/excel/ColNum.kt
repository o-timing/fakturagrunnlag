package otiming.fakturagrunnlag.excel

data class ColNum(val value: Int) {
    fun render(): String {
        return ('A' + value).toString()
    }

    fun add(i: ColNum): ColNum {
        return ColNum(value + i.value)
    }
}