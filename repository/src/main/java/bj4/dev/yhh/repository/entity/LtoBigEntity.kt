package bj4.dev.yhh.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import bj4.dev.yhh.lottery_parser.LotteryRawData
import bj4.dev.yhh.repository.CellData

@Entity
data class LtoBigEntity(
    @field:[PrimaryKey]
    val timeStamp: Long,

    val rawNormalNumbers: List<Int>,

    val rawSpecialNumbers: List<Int>,

    val column1: List<CellData>,

    val isSubTotal: Boolean = false
)