package com.example.composeapp.domain.usecase

import com.example.composeapp.data.enitities.translation.Ayah
import com.example.composeapp.data.repositories.QuranDataRepository

class GetTafsirUseCase(private val repository: QuranDataRepository) {
    suspend operator fun invoke(): Map<String, Map<String, Ayah>> = repository.getTafsir()
}
