package bj4.dev.yhh.log.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import bj4.dev.yhh.log.room.dao.LogDao
import bj4.dev.yhh.log.room.entity.JobServiceEntity
import bj4.dev.yhh.log.room.entity.UpdateServiceTimeEntity

@Database(
    entities = [UpdateServiceTimeEntity::class, JobServiceEntity::class],
    version = 1
)
internal abstract class LogDatabase : RoomDatabase() {
    abstract fun getLogDao(): LogDao
}