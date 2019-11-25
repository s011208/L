package bj4.dev.yhh.repository.database

import android.content.Context
import androidx.room.Room

class LotteryDatabaseHelper(context: Context) {

    companion object {
        private const val DATABASE_NAME = "lottery.db"
    }

    val database = Room.databaseBuilder(context.applicationContext, LotteryDatabase::class.java, DATABASE_NAME).build()
}