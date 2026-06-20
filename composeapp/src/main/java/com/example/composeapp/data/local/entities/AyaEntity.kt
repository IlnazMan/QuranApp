package com.example.composeapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ayas", primaryKeys = ["suraIndex", "ayaIndex"])
data class AyaEntity(
    val suraIndex: String,
    val ayaIndex: String,
    val text: String?,
    val bismillah: String?
)
