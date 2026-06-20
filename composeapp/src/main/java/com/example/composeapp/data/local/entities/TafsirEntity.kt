package com.example.composeapp.data.local.entities

import androidx.room.Entity

@Entity(tableName = "tafsir", primaryKeys = ["suraKey", "ayahKey"])
data class TafsirEntity(
    val suraKey: String, // e.g., "sura-1"
    val ayahKey: String, // e.g., "ayah-1"
    val tafsir: String?,
    val note: String?
)
