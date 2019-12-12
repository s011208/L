package bj4.dev.yhh.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.repository.entity.LtoList4Entity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LtoList4Dao {

    @Query("SELECT * FROM `LtoList4Entity` ORDER BY `timeStamp`")
    fun query(): Single<List<LtoList4Entity>>

    @Query("SELECT * FROM `LtoList4Entity` ORDER BY `timeStamp`")
    fun queryLiveData(): Flowable<List<LtoList4Entity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(items: LtoList4Entity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<LtoList4Entity>): List<Long>

    @Query("DELETE FROM `LtoList4Entity`")
    fun nukeTable()
}