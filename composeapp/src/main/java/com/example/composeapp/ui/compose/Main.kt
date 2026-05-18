package com.example.composeapp.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.utils.playVibration
import com.example.composeapp.viewmodel.ThemeViewModel

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
    onSurahClick: (SurahItem) -> Unit,
    suraList: List<SurahItem>,
    themeViewModel: ThemeViewModel
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("QuranApp")
                },
                actions = {
                    IconButton(onClick = { themeViewModel.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Dark Mode"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            items(suraList) { surah ->
                SurahListItem(
                    surah = surah,
                    onClick = {
                        playVibration(context)
                        onSurahClick(surah)
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun SurahListItem(
    surah: SurahItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(
            text = surah.title?.tat ?: surah.title?.rus ?: "",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = surah.titleAr ?: "",
            fontFamily = fontFamily,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
