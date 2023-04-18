package ru.umma.ummaruapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SURAH_TABLE_NAME)
data class Surahs(
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "link")
    val link: String,
    @PrimaryKey
    @ColumnInfo(name = "number")
    val number: String
)

@Entity(tableName = AYAH_TABLE_NAME)
data class Ayahs(
    @PrimaryKey
    @ColumnInfo(name = "number")
    val number: String,
    @ColumnInfo(name = "arabic")
    val arabic: String,
    @ColumnInfo(name = "transcription")
    val transcription: String,
    @ColumnInfo(name = "translate")
    val translate: String,
    @ColumnInfo(name = "explanation")
    val explanation: String,
    @ColumnInfo(name = "surah_number")
    val surahNumber: String,
)

@Entity(tableName = LAST_DOWNLOAD_INFO_TABLE_NAME)
data class LastDownload(
    @PrimaryKey
    @ColumnInfo(name = "download_time")
    val downloadTime: Long,
)

const val SURAH_TABLE_NAME = "surah"
const val AYAH_TABLE_NAME = "ayah"
const val LAST_DOWNLOAD_INFO_TABLE_NAME = "download_info"