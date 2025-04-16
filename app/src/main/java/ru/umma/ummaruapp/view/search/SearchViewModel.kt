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
    private val _surahListLiveData = MutableLiveData<SearchResult>()
    val surahListLiveData: LiveData<SearchResult> = _surahListLiveData

    suspend fun findAyas(ayah: String) {
        _surahListLiveData.postValue(SearchResult.Loading)
        val ayahs = provider.searchAyahs(ayah)

        val result = if (ayahs.isEmpty()) {
            SearchResult.Empty
        } else {
            SearchResult.Data(ayahs)
        }
        _surahListLiveData.postValue(result)
    }
}

sealed interface SearchResult {
    class Data(
        val result: List<Pair<AyahBlock, Surah>>,
    ) : SearchResult

    data object Loading : SearchResult

    data object Empty : SearchResult
}