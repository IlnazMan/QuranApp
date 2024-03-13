package ru.umma.ummaruapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import ru.umma.ummaruapp.di.appModule
import ru.umma.ummaruapp.di.viewModels

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, viewModels))
        }
    }


}