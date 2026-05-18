package com.example.composeapp.data.repositories

import android.content.Context
import com.example.composeapp.data.enitities.quran.QuranData
import com.example.composeapp.data.enitities.quran.SuraData
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.data.enitities.translation.Ayah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/**
 * @author i.m.mannapov
 */
class QuranDataRepositoryImpl(private val context: Context) : QuranDataRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getSurahList(): List<SurahItem> = withContext(Dispatchers.IO) {
        // Assuming suras.json contains the list of SurahItem
        try {
            context.assets.open("suras.json").use {
                json.decodeFromStream<List<SurahItem>>(it)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getSurah(suraId: String): SuraData = withContext(Dispatchers.IO) {
        val quranData = getQuranData()
        val index = suraId.toIntOrNull() ?: -1
        quranData.suraList.find { it.attributes?.index?.toIntOrNull() == index }
            ?: throw Exception("Surah not found")
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getTafsir(): Map<String, Map<String, Ayah>> = withContext(Dispatchers.IO) {
        context.assets.open("tafsir.json").use {
            json.decodeFromStream<Map<String, Map<String, Ayah>>>(it)
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun getQuranData(): QuranData = withContext(Dispatchers.IO) {
        context.assets.open("quran.json").use {
            json.decodeFromStream<QuranData>(it)
        }
    }
}
