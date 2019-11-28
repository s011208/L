package bj4.dev.yhh.log

import android.content.Context
import bj4.dev.yhh.log.room.database.LogDatabaseHelper
import bj4.dev.yhh.log.room.entity.JobServiceEntity
import bj4.dev.yhh.log.room.entity.UpdateServiceTimeEntity

class LogHelper(context: Context) {
    private val databaseHelper = LogDatabaseHelper(context)

    fun queryUpdateServiceTimeEntityList() =
        databaseHelper.database.getLogDao().queryUpdateServiceTimeEntity()

    fun insert(item: UpdateServiceTimeEntity) =
        databaseHelper.database.getLogDao().insert(item)

    fun queryJobServiceEntityList() = databaseHelper.database.getLogDao().queryJobServiceEntity()

    fun insert(item: JobServiceEntity) =
        databaseHelper.database.getLogDao().insert(item)
}