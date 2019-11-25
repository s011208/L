package bj4.dev.yhh.repository

data class CellData(
    val id: Int,
    var value: Int,
    val isNormalNumber: Boolean = false,
    val isSpecialNumber: Boolean = false
)