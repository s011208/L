package bj4.dev.yhh.log.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UpdateServiceTimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timeStamp: Long = System.currentTimeMillis(),
    val message: String
)