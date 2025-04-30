package com.example.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.ui.compose.MyNavHost
import com.example.composeapp.ui.theme.QuranAppTheme
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val str = Json.decodeFromStream<List<SurahItem>>(assets.open("suras.json"))
        setContent {
            QuranAppTheme {
                MyNavHost(suraList = str)
            }
        }
    }
}

/*
* https://tat-tts.api.translate.tatar/listening/?speaker=almaz&text=алты+ике+дурт
* val str = Json.decodeFromStream<QuranData>(this.assets.open("quran.json"))

* */