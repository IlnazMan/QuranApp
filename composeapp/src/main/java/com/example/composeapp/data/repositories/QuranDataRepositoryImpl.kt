package com.example.composeapp.data.repositories

import android.content.Context
import com.example.composeapp.data.enitities.quran.Attributes
import com.example.composeapp.data.enitities.quran.AttributesX
import com.example.composeapp.data.enitities.quran.Aya
import com.example.composeapp.data.enitities.quran.QuranData
import com.example.composeapp.data.enitities.quran.SuraData
import com.example.composeapp.data.enitities.suralist.Juz
import com.example.composeapp.data.enitities.suralist.Place
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.data.enitities.suralist.Title
import com.example.composeapp.data.enitities.translation.Ayah
import com.example.composeapp.data.local.QuranDao
import com.example.composeapp.data.local.entities.AyaEntity
import com.example.composeapp.data.local.entities.SurahEntity
import com.example.composeapp.data.local.entities.TafsirEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

/**
 * @author i.m.mannapov
 */
class QuranDataRepositoryImpl(
    private val context: Context,
    private val quranDao: QuranDao
) : QuranDataRepository {

    private val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getSurahList(): List<SurahItem> = withContext(Dispatchers.IO) {
        val count = quranDao.getSurahsCount()
        if (count == 0) {
            val surahs = loadSurahsFromAssets()
            saveSurahsToDb(surahs)
            surahs
        } else {
            quranDao.getAllSurahs().map { entity ->
                SurahItem(
                    count = entity.count,
                    index = entity.index,
                    juz = entity.juzJson?.let { json.decodeFromString<List<Juz?>>(it) },
                    pages = entity.pages,
                    place = Place(rus = entity.placeRus, tat = entity.placeTat),
                    title = Title(rus = entity.titleRus, tat = entity.titleTat),
                    titleAr = entity.titleAr,
                    titleTranslate = null, // Not used in UI yet
                    type = entity.type
                )
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getSurah(suraId: String): SuraData = withContext(Dispatchers.IO) {
        val suraId = suraId.trimStart('0')
        val ayas = quranDao.getAyasForSurah(suraId)
        if (ayas.isEmpty()) {
            // If not in DB, maybe we need to populate the whole QuranData
            val quranData = loadQuranDataFromAssets()
            saveQuranDataToDb(quranData)
            quranData.suraList.find { it.attributes?.index == suraId }
                ?: throw Exception("Surah not found")
        } else {
            SuraData(
                attributes = Attributes(index = suraId, name = null),
                aya = ayas.map {
                    Aya(
                        attributes = AttributesX(
                            text = it.text,
                            index = it.ayaIndex,
                            bismillah = it.bismillah
                        )
                    )
                }
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getTafsir(): Map<String, Map<String, Ayah>> = withContext(Dispatchers.IO) {
        val entities = quranDao.getAllTafsir()
        if (entities.isEmpty()) {
            val tafsir = loadTafsirFromAssets()
            saveTafsirToDb(tafsir)
            tafsir
        } else {
            entities.groupBy { it.suraKey }.mapValues { entry ->
                entry.value.associate { it.ayahKey to Ayah(tafsir = it.tafsir, note = it.note) }
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadSurahsFromAssets(): List<SurahItem> {
        return try {
            context.assets.open("suras.json").use {
                json.decodeFromStream<List<SurahItem>>(it)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun saveSurahsToDb(surahs: List<SurahItem>) {
        val entities = surahs.map { item ->
            SurahEntity(
                index = item.index ?: "",
                count = item.count,
                juzJson = item.juz?.let { json.encodeToString(it) },
                pages = item.pages,
                placeRus = item.place?.rus,
                placeTat = item.place?.tat,
                titleRus = item.title?.rus,
                titleTat = item.title?.tat,
                titleAr = item.titleAr,
                type = item.type
            )
        }
        quranDao.insertSurahs(entities)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadQuranDataFromAssets(): QuranData {
        return context.assets.open("quran.json").use {
            json.decodeFromStream<QuranData>(it)
        }
    }

    private suspend fun saveQuranDataToDb(quranData: QuranData) {
        val ayas = quranData.suraList.flatMap { sura ->
            val suraIndex = sura.attributes?.index ?: ""
            sura.aya?.map { aya ->
                AyaEntity(
                    suraIndex = suraIndex,
                    ayaIndex = aya.attributes?.index ?: "",
                    text = aya.attributes?.text,
                    bismillah = aya.attributes?.bismillah
                )
            } ?: emptyList()
        }
        quranDao.insertAyas(ayas)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun loadTafsirFromAssets(): Map<String, Map<String, Ayah>> {
        return context.assets.open("tafsir.json").use {
            json.decodeFromStream<Map<String, Map<String, Ayah>>>(it)
        }
    }

    private suspend fun saveTafsirToDb(tafsir: Map<String, Map<String, Ayah>>) {
        val entities = tafsir.flatMap { suraEntry ->
            suraEntry.value.map { ayaEntry ->
                TafsirEntity(
                    suraKey = suraEntry.key,
                    ayahKey = ayaEntry.key,
                    tafsir = ayaEntry.value.tafsir,
                    note = ayaEntry.value.note
                )
            }
        }
        quranDao.insertTafsir(entities)
    }
}
