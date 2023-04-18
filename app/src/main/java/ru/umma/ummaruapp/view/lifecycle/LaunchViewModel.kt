package ru.umma.ummaruapp.view.lifecycle

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.umma.ummaruapp.domain.DataProvider
import ru.umma.ummaruapp.domain.IQuranProvider

class LaunchViewModel(
    private val dbProvider: IQuranProvider,
    private val dataProvider: DataProvider
) : ViewModel() {
    private val _dataIsReady = MutableStateFlow(false)
    val dataIsReady: LiveData<Boolean> = _dataIsReady.asLiveData()
    val progress = dataProvider.downloadProgress.asLiveData()
    private val _error = MutableStateFlow<String?>(null)
    val error: LiveData<String?> = _error.asLiveData()

    init {
        downloadAll()
    }

    fun retry() {
        downloadAll()
    }

    private fun downloadAll() {
        viewModelScope.launch(Dispatchers.IO) {
            _error.tryEmit(null)
            try {
                if (!dbProvider.isDataActual()) {
                    dataProvider.downloadAll()
                }
                _dataIsReady.tryEmit(true)
            } catch (e: Throwable) {
                e.printStackTrace()
                _error.tryEmit(e.message)
            }
        }
    }
}