package bj4.dev.yhh.log.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import bj4.dev.yhh.log.room.entity.JobServiceEntity
import bj4.dev.yhh.log.room.entity.UpdateServiceTimeEntity
import io.reactivex.Single

@Dao
internal interface LogDao {
    @Query("SELECT * FROM `UpdateServiceTimeEntity`")
    fun queryUpdateServiceTimeEntity(): Single<UpdateServiceTimeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: UpdateServiceTimeEntity): Long

    @Query("SELECT * FROM `JobServiceEntity`")
    fun queryJobServiceEntity(): Single<JobServiceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(item: JobServiceEntity): Long
}