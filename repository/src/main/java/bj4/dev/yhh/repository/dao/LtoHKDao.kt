package bj4.dev.yhh.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.repository.entity.LtoHKEntity
import io.reactivex.Single

@Dao
interface LtoHKDao {

    @Query("SELECT * FROM `LtoHKEntity` ORDER BY `timeStamp`")
    fun query(): Single<List<LtoHKEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(items: LtoHKEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<LtoHKEntity>): List<Long>
}