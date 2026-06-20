package com.example.composeapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surahs")
data class SurahEntity(
    @PrimaryKey val index: String,
    val count: Int?,
    val juzJson: String?,
    val pages: String?,
    val placeTat: String?,
    val placeRus: String?,
    val titleTat: String?,
    val titleRus: String?,
    val titleAr: String?,
    val type: String?
)
