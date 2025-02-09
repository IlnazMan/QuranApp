package ru.umma.ummaruapp.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.domain.IQuranProvider

class SearchViewModel(
    private val provider: IQuranProvider,
) : ViewModel() {
    private val _surahListLiveData = MutableLiveData<List<Pair<AyahBlock, Surah>>>()
    val surahListLiveData: LiveData<List<Pair<AyahBlock, Surah>>> = _surahListLiveData

    suspend fun findAyas(ayah: String) {
        val ayahs = provider.searchAyahs(ayah)
        _surahListLiveData.postValue(ayahs)
    }
}