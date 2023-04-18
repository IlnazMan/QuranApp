package ru.umma.ummaruapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase

@androidx.room.Database(
    entities = [Ayahs::class, Surahs::class, LastDownload::class], version = 1
)
abstract class Database : RoomDatabase() {
    abstract fun holyQuranDao(): QuranDao
}

@Dao
interface QuranDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurahs(surahs: Collection<Surahs>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahs(ayahs: Collection<Ayahs>)

    @Query(
        """
        SELECT *
        FROM $SURAH_TABLE_NAME
    """
    )
    suspend fun getAllSurahs(): List<Surahs>

    @Query(
        """
        SELECT *
        FROM $AYAH_TABLE_NAME
        WHERE surah_number == :sNmb
    """
    )
    suspend fun getAyahBySurahNumber(sNmb: String): List<Ayahs>

    @Query(
        """
            SELECT *
        FROM $LAST_DOWNLOAD_INFO_TABLE_NAME
        """
    )
    suspend fun getLastDownloadTime(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastDownload(lastDownload: LastDownload)
}