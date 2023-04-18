package ru.umma.ummaruapp.domain

import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.data.models.Surah

interface IQuranProvider {
    suspend fun getSurahList(): List<Surah>

    suspend fun getSurahContent(surah: Surah): List<AyahBlock>

    suspend fun isDataActual(): Boolean
}