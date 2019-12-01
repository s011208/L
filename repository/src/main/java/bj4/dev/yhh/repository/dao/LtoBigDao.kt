package bj4.dev.yhh.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.repository.entity.LtoBigEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface LtoBigDao {

    @Query("SELECT * FROM `LtoBigEntity` ORDER BY `timeStamp`")
    fun query(): Single<List<LtoBigEntity>>

    @Query("SELECT * FROM `LtoBigEntity` ORDER BY `timeStamp`")
    fun queryLiveData(): Flowable<List<LtoBigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(items: LtoBigEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<LtoBigEntity>): List<Long>

    @Query("DELETE FROM `LtoBigEntity`")
    fun nukeTable()
}