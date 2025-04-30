package com.example.composeapp.data.repositories

import com.example.composeapp.data.enitities.quran.SuraData
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.data.enitities.translation.Ayah

/**
 * @author i.m.mannapov
 */
interface QuranDataRepository {

    suspend fun getSurahList(): List<SurahItem>

    suspend fun getSurah(suraId: String): SuraData

    suspend fun getTafsir(): Map<String, Map<String, Ayah>>
}