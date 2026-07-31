package com.example.rafiq.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Query("SELECT * FROM equipped_places")
    fun getAllPlaces(): Flow<List<EquippedPlaceEntity>>

    @Query("SELECT * FROM equipped_places WHERE id = :id")
    suspend fun getPlaceById(id: String): EquippedPlaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: EquippedPlaceEntity)

    @Delete
    suspend fun deletePlace(place: EquippedPlaceEntity)
}
