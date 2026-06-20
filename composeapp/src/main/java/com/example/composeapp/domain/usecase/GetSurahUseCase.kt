package com.example.composeapp.domain.usecase

import com.example.composeapp.data.enitities.quran.SuraData
import com.example.composeapp.data.repositories.QuranDataRepository

class GetSurahUseCase(private val repository: QuranDataRepository) {
    suspend operator fun invoke(suraId: String): SuraData = repository.getSurah(suraId)
}
