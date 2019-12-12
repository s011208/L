package bj4.dev.yhh.repository.entity

import bj4.dev.yhh.repository.LotteryType

abstract class LotteryEntity {
    companion object {
        fun getTimeStamp(@LotteryType lotteryType: Int, entity: LotteryEntity): Long {
            return when (lotteryType) {
                LotteryType.LtoBig -> (entity as LtoBigEntity).timeStamp
                LotteryType.Lto -> (entity as LtoEntity).timeStamp
                LotteryType.LtoHK -> (entity as LtoHKEntity).timeStamp
                LotteryType.LtoList3 -> (entity as LtoList3Entity).timeStamp
                LotteryType.LtoList4 -> (entity as LtoList4Entity).timeStamp
                else -> throw IllegalArgumentException("Unknown type")
            }
        }
    }
}