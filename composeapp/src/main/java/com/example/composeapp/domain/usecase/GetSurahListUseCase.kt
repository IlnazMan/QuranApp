package com.example.composeapp.domain.usecase

import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.data.repositories.QuranDataRepository

class GetSurahListUseCase(private val repository: QuranDataRepository) {
    suspend operator fun invoke(): List<SurahItem> = repository.getSurahList()
}
