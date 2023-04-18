package ru.umma.ummaruapp.data.db

import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.domain.IQuranProvider

class DatabaseDataProvider(private val _db: Database) : IQuranProvider {
    override suspend fun getSurahList(): List<Surah> {
        return _db.holyQuranDao().getAllSurahs().map {
            Surah(
                it.name,
                it.link,
                it.number
            )
        }
    }

    override suspend fun getSurahContent(surah: Surah): List<AyahBlock> {
        return _db.holyQuranDao().getAyahBySurahNumber(surah.number).map {
            AyahBlock(
                it.number,
                it.arabic,
                it.transcription,
                it.translate,
                it.explanation
            )
        }
    }

    override suspend fun isDataActual(): Boolean {
        return ((_db.holyQuranDao().getLastDownloadTime() ?: 0L)) > 0
    }
}