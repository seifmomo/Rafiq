package com.example.rafiq.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EquippedPlaceEntity::class, Contact::class, Medication::class, ChatMessage::class],
    version = 2,
    exportSchema = true
)
abstract class RafiqDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun contactDao(): ContactDao
    abstract fun medicationDao(): MedicationDao
    abstract fun chatMessageDao(): ChatMessageDao
}
