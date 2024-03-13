package ru.umma.ummaruapp.domain

import kotlinx.coroutines.flow.StateFlow
import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.data.models.Surah

interface DataProvider {
    val downloadProgress: StateFlow<Pair<Int, Int>>

    suspend fun downloadAll()
}