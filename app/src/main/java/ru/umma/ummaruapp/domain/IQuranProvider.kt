package ru.umma.ummaruapp.domain

import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.data.models.Surah

interface IQuranProvider {
    suspend fun getSurahList(): List<Surah>

    suspend fun getSurahContent(surah: Surah): List<AyahBlock>

    suspend fun searchAyahs(text: String): List<Pair<AyahBlock, Surah>>

    suspend fun isDataActual(): Boolean
}