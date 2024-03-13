package ru.umma.ummaruapp.view.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.umma.ummaruapp.domain.DataProvider
import ru.umma.ummaruapp.domain.IQuranProvider

class LaunchViewModel(
    private val dbProvider: IQuranProvider,
    private val dataProvider: DataProvider
) : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state: LiveData<State> = _state.asLiveData().distinctUntilChanged()

    init {
        viewModelScope.launch {
            dataProvider.downloadProgress.collectLatest {
                val value = _state.value
                _state.emit(value.copy(progress = it))
            }
        }
        downloadAll()
    }

    fun retry() {
        downloadAll()
    }

    private fun downloadAll() {
        val value = _state.value
        viewModelScope.launch(Dispatchers.IO) {
            _state.emit(value.copy(error = null))
            try {
                if (!dbProvider.isDataActual()) {
                    dataProvider.downloadAll()
                }
                _state.emit(value.copy(dataIsReady = true, error = null))
            } catch (e: Throwable) {
                e.printStackTrace()
                _state.emit(value.copy(error = e.message))
            }
        }
    }
}

data class State(
    val dataIsReady: Boolean = false,
    val error: String? = null,
    val progress: Pair<Int, Int> = 0 to 1,
)

