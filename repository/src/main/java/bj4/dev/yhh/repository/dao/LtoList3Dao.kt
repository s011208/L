package bj4.dev.yhh.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.repository.entity.LtoList3Entity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LtoList3Dao {

    @Query("SELECT * FROM `LtoList3Entity` ORDER BY `timeStamp`")
    fun query(): Single<List<LtoList3Entity>>

    @Query("SELECT * FROM `LtoList3Entity` ORDER BY `timeStamp`")
    fun queryLiveData(): Flowable<List<LtoList3Entity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(items: LtoList3Entity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<LtoList3Entity>): List<Long>

    @Query("DELETE FROM `LtoList3Entity`")
    fun nukeTable()
}