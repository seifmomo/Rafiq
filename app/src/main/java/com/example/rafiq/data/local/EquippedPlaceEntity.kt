package com.example.rafiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipped_places")
data class EquippedPlaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val isWheelchairAccessible: Boolean,
    val hasSignLanguageSupport: Boolean,
    val hasBrailleSignage: Boolean
)
