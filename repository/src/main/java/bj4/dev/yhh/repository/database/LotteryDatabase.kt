package bj4.dev.yhh.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import bj4.dev.yhh.repository.converter.LotteryRawDataConverter
import bj4.dev.yhh.repository.dao.*
import bj4.dev.yhh.repository.entity.*

@Database(
    entities = [
        LotteryResultEntity::class,
        LtoList3Entity::class,
        LtoList4Entity::class,
        LtoHKEntity::class,
        LtoBigEntity::class,
        LtoEntity::class],
    version = 1
)
@TypeConverters(LotteryRawDataConverter::class)
abstract class LotteryDatabase : RoomDatabase() {

    abstract fun getResultDao(): LotteryResultDao

    abstract fun getLtoHKDao(): LtoHKDao

    abstract fun getLtoBigDao(): LtoBigDao

    abstract fun getLtoDao(): LtoDao

    abstract fun getLtoList3Dao(): LtoList3Dao

    abstract fun getLtoList4Dao(): LtoList4Dao
}