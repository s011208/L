package bj4.dev.yhh.repository.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class LotteryResultEntity(
    val done: Boolean = false,

    @field:[PrimaryKey]
    val lotteryType: Int
)