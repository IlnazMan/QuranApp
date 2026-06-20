package com.example.composeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.composeapp.data.local.entities.AyaEntity
import com.example.composeapp.data.local.entities.SurahEntity
import com.example.composeapp.data.local.entities.TafsirEntity

@Dao
interface QuranDao {

    @Query("SELECT * FROM surahs")
    suspend fun getAllSurahs(): List<SurahEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: List<SurahEntity>)

    @Query("SELECT * FROM ayas WHERE suraIndex = :suraIndex")
    suspend fun getAyasForSurah(suraIndex: String): List<AyaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyas(ayas: List<AyaEntity>)

    @Query("SELECT * FROM tafsir")
    suspend fun getAllTafsir(): List<TafsirEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTafsir(tafsir: List<TafsirEntity>)

    @Query("SELECT COUNT(*) FROM surahs")
    suspend fun getSurahsCount(): Int
}
