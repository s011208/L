package bj4.dev.yhh.log.room.database

import android.content.Context
import androidx.room.Room

class LogDatabaseHelper(context: Context) {
    companion object {
        private const val DATABASE_NAME = "log.db"
    }

    internal val database = Room.databaseBuilder(context.applicationContext, LogDatabase::class.java, DATABASE_NAME).build()

}