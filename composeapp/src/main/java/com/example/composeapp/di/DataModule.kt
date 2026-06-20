package com.example.composeapp.di

import android.content.Context
import androidx.room.Room
import com.example.composeapp.data.local.QuranDao
import com.example.composeapp.data.local.QuranDatabase
import com.example.composeapp.data.repositories.QuranDataRepository
import com.example.composeapp.data.repositories.QuranDataRepositoryImpl
import com.example.composeapp.domain.usecase.GetSurahListUseCase
import com.example.composeapp.domain.usecase.GetSurahUseCase
import com.example.composeapp.domain.usecase.GetTafsirUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideQuranDatabase(@ApplicationContext context: Context): QuranDatabase {
        return Room.databaseBuilder(
            context,
            QuranDatabase::class.java,
            "quran_db"
        ).build()
    }

    @Provides
    fun provideQuranDao(database: QuranDatabase): QuranDao {
        return database.quranDao()
    }

    @Provides
    @Singleton
    fun provideQuranRepository(
        @ApplicationContext context: Context,
        quranDao: QuranDao
    ): QuranDataRepository {
        return QuranDataRepositoryImpl(context, quranDao)
    }

    @Provides
    @Singleton
    fun provideGetSurahListUseCase(repository: QuranDataRepository): GetSurahListUseCase {
        return GetSurahListUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetSurahUseCase(repository: QuranDataRepository): GetSurahUseCase {
        return GetSurahUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetTafsirUseCase(repository: QuranDataRepository): GetTafsirUseCase {
        return GetTafsirUseCase(repository)
    }
}
