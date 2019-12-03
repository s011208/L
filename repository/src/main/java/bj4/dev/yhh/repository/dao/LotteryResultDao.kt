package bj4.dev.yhh.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.repository.LotteryType
import bj4.dev.yhh.repository.entity.LotteryResultEntity

@Dao
interface LotteryResultDao {
    @Query("SELECT * FROM `LotteryResultEntity` WHERE `lotteryType`=:type")
    fun query(@LotteryType type: Int): List<LotteryResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: LotteryResultEntity): Long

    @Query("DELETE FROM `LotteryResultEntity`")
    fun nukeTable()
}