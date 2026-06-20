package com.example.composeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.enitities.quran.SuraData
import com.example.composeapp.data.enitities.translation.Ayah
import com.example.composeapp.domain.usecase.GetSurahUseCase
import com.example.composeapp.domain.usecase.GetTafsirUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurahViewModel @Inject constructor(
    private val getSurahUseCase: GetSurahUseCase,
    private val getTafsirUseCase: GetTafsirUseCase
) : ViewModel() {
    private val _suraData = MutableStateFlow<SuraData?>(null)
    val suraData: StateFlow<SuraData?> = _suraData

    private val _tafsirData = MutableStateFlow<Map<String, Map<String, Ayah>>>(emptyMap())
    val tafsirData: StateFlow<Map<String, Map<String, Ayah>>> = _tafsirData

    fun loadSurahData(suraId: String) {
        viewModelScope.launch {
            try {
                _suraData.value = getSurahUseCase(suraId)
                _tafsirData.value = getTafsirUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
