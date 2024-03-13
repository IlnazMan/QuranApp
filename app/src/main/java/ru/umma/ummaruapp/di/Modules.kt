package ru.umma.ummaruapp.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.umma.ummaruapp.data.Prefs
import ru.umma.ummaruapp.data.db.Database
import ru.umma.ummaruapp.data.db.DatabaseDataProvider
import ru.umma.ummaruapp.data.models.NetworkDataProvider
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.domain.DataProvider
import ru.umma.ummaruapp.domain.IQuranProvider
import ru.umma.ummaruapp.view.launch.LaunchViewModel
import ru.umma.ummaruapp.view.main.MainViewModel
import ru.umma.ummaruapp.view.surah.SurahViewModel

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(), Database::class.java, "quran_tafsir"
        ).build()
    }
    single<DataProvider> {
        NetworkDataProvider(get())
    }
    single<IQuranProvider>() {
        DatabaseDataProvider(get())
    }
    single {
        Prefs(androidContext())
    }
}

val viewModels = module {
    viewModel {
        MainViewModel(get(), get())
    }
    viewModel {
        LaunchViewModel(get(), get())
    }
    viewModel { (s: Surah) ->
        SurahViewModel(s, get(), get())
    }
}
