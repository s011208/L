package bj4.dev.yhh.repository.entity

import bj4.dev.yhh.repository.LotteryType

abstract class LotteryEntity {
    companion object {
        fun getTimeStamp(@LotteryType lotteryType: Int, entity: LotteryEntity): Long {
            return when (lotteryType) {
                LotteryType.LtoBig -> (entity as LtoBigEntity).timeStamp
                LotteryType.Lto -> (entity as LtoEntity).timeStamp
                LotteryType.LtoHK -> (entity as LtoHKEntity).timeStamp
                else -> throw IllegalArgumentException("Unknown type")
            }
        }
    }
}