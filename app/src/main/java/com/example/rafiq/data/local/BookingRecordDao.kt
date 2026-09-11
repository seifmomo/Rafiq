package com.example.rafiq.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BookingRecordEntity)

    @Query("SELECT * FROM booking_records ORDER BY confirmedAt DESC")
    fun observeAll(): Flow<List<BookingRecordEntity>>

    @Query("SELECT * FROM booking_records WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): BookingRecordEntity?

    @Query("UPDATE booking_records SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: String, rating: Int)

    @Query("DELETE FROM booking_records WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query(
        "SELECT AVG(rating) FROM booking_records WHERE assistantId = :assistantId AND rating IS NOT NULL"
    )
    suspend fun averageRatingFor(assistantId: String): Double?
}