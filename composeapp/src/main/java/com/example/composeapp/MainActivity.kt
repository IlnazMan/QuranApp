package com.example.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.ui.compose.MyNavHost
import com.example.composeapp.ui.theme.QuranAppTheme
import com.example.composeapp.viewmodel.ThemeViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class MainActivity : ComponentActivity() {

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val str = Json.decodeFromStream<List<SurahItem>>(assets.open("suras.json"))
        setContent {
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            
            QuranAppTheme(darkTheme = isDarkMode) {
                MyNavHost(
                    suraList = str,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}
