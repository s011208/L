package bj4.dev.yhh.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.repository.entity.LtoEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LtoDao {

    @Query("SELECT * FROM `LtoEntity` ORDER BY `timeStamp`")
    fun query(): Single<List<LtoEntity>>

    @Query("SELECT * FROM `LtoEntity` ORDER BY `timeStamp`")
    fun queryLiveData(): Flowable<List<LtoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(items: LtoEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<LtoEntity>): List<Long>

    @Query("DELETE FROM `LtoEntity`")
    fun nukeTable()
}