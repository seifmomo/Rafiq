package com.example.rafiq.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey
    val id: String,
    val name: String,
    val dosage: String,
    val time: String
)
