package bj4.dev.yhh.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import bj4.dev.yhh.lottery_parser.LotteryRawData
import bj4.dev.yhh.repository.CellData

@Entity
data class LtoList3Entity(
    @field:[PrimaryKey]
    var timeStamp: Long = 0,

    var rawNormalNumbers: List<Int> = ArrayList()
): LotteryEntity()