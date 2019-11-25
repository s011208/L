package bj4.dev.yhh.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import bj4.dev.yhh.repository.converter.LotteryRawDataConverter
import bj4.dev.yhh.repository.dao.LotteryResultDao
import bj4.dev.yhh.repository.dao.LtoBigDao
import bj4.dev.yhh.repository.dao.LtoDao
import bj4.dev.yhh.repository.dao.LtoHKDao
import bj4.dev.yhh.repository.entity.LotteryResultEntity
import bj4.dev.yhh.repository.entity.LtoBigEntity
import bj4.dev.yhh.repository.entity.LtoEntity
import bj4.dev.yhh.repository.entity.LtoHKEntity

@Database(
    entities = [LotteryResultEntity::class, LtoHKEntity::class, LtoBigEntity::class, LtoEntity::class],
    version = 1
)
@TypeConverters(LotteryRawDataConverter::class)
abstract class LotteryDatabase : RoomDatabase() {

    abstract fun getResultDao(): LotteryResultDao

    abstract fun getLtoHKDao(): LtoHKDao

    abstract fun getLtoBigDao(): LtoBigDao

    abstract fun getLtoDao(): LtoDao
}