package com.example.composeapp.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.composeapp.R
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.ui.theme.BISMILLAH

/**
 * @author i.m.mannapov
 */
val fontFamily = FontFamily(
    Font(R.font.kazan_basma)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(
    modifier: Modifier = Modifier,
    onSurahClick: () -> Unit,
    suraList: List<SurahItem>,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("QuranApp")
                },
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            LazyColumn {
                item {
                    Text(
                        text = BISMILLAH,
                        fontFamily = fontFamily,
                        modifier = Modifier.clickable() {
                            onSurahClick()
                        }
                    )
                }
                items(suraList.size) {
                    val surah = suraList[it]

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = surah.titleAr.orEmpty(),
                        fontFamily = fontFamily,
                    )
                }
            }
        }
    }
}