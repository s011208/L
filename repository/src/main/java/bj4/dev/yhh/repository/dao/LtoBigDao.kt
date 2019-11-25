package bj4.dev.yhh.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.repository.entity.LtoBigEntity
import io.reactivex.Single

@Dao
interface LtoBigDao {

    @Query("SELECT * FROM `LtoBigEntity` ORDER BY `timeStamp`")
    fun query(): Single<List<LtoBigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReplace(items: LtoBigEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<LtoBigEntity>): List<Long>
}