package com.example.composeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.composeapp.data.local.entities.AyaEntity
import com.example.composeapp.data.local.entities.SurahEntity
import com.example.composeapp.data.local.entities.TafsirEntity

@Database(entities = [SurahEntity::class, AyaEntity::class, TafsirEntity::class], version = 1)
abstract class QuranDatabase : RoomDatabase() {
    abstract fun quranDao(): QuranDao
}
