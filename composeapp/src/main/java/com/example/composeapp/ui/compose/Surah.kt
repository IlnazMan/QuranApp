package com.example.composeapp.ui.compose

import android.content.Context
import android.media.MediaPlayer
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.data.enitities.quran.SuraData
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.data.enitities.translation.Ayah
import com.example.composeapp.data.repositories.QuranDataRepository
import com.example.composeapp.data.repositories.QuranDataRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLEncoder

private val json = Json { ignoreUnknownKeys = true }

sealed class JuzMarker {
    data class Start(val index: String) : JuzMarker()
    data class Continuation(val index: String) : JuzMarker()
}

/**
 * @author i.m.mannapov
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurahScreen(
    surah: SurahItem?,
    allSuras: List<SurahItem>,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository: QuranDataRepository = remember { QuranDataRepositoryImpl(context) }
    val coroutineScope = rememberCoroutineScope()
    var showTranslation by remember { mutableStateOf(true) }

    var currentSuraData by remember { mutableStateOf<SuraData?>(null) }
    var tafsirData by remember { mutableStateOf<Map<String, Map<String, Ayah>>>(emptyMap()) }

    // Логика определения начала или продолжения джуза
    val juzMarkers = remember(surah, allSuras) {
        if (surah == null) return@remember emptyMap<String, JuzMarker>()

        val markers = mutableMapOf<String, JuzMarker>()
        val currentSuraIndex = allSuras.indexOf(surah)
        val prevSura = if (currentSuraIndex > 0) allSuras[currentSuraIndex - 1] else null

        surah.juz?.filterNotNull()?.forEach { j ->
            val indexStr = j.index ?: ""
            val startVerse = j.verse?.start?.removePrefix("verse_") ?: ""

            if (startVerse == "1") {
                // Если сура начинается с этого джуза, проверяем был ли он в предыдущей суре
                val wasInPrevSura = prevSura?.juz?.any { it?.index == indexStr } ?: false
                if (wasInPrevSura) {
                    markers[startVerse] = JuzMarker.Continuation(indexStr)
                } else {
                    markers[startVerse] = JuzMarker.Start(indexStr)
                }
            } else {
                // Если джуз начинается не с 1-го аята суры, это всегда начало нового джуза
                markers[startVerse] = JuzMarker.Start(indexStr)
            }
        }
        markers
    }

    LaunchedEffect(surah) {
        surah?.index?.let { index ->
            try {
                currentSuraData = repository.getSurah(index)
                tafsirData = repository.getTafsir()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Управление аудио
    val mediaPlayer = remember { MediaPlayer() }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(surah?.title?.tat ?: surah?.title?.rus ?: "Surah")
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showTranslation = !showTranslation }) {
                        Icon(
                            imageVector = if (showTranslation) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Translation"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (currentSuraData != null) {
                // Info block
                val count = surah?.count ?: 0
                val place = surah?.place?.tat ?: surah?.place?.rus ?: ""

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$place • $count аять",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 1. Извлекаем Bismillah
                val bismillahText = currentSuraData?.aya?.firstOrNull()?.attributes?.bismillah

                if (!bismillahText.isNullOrBlank()) {
                    Text(
                        text = bismillahText,
                        fontFamily = fontFamily,
                        fontSize = 32.sp,
                        lineHeight = 50.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                }

                val suraIndex = surah?.index?.toIntOrNull() ?: -1
                val suraKey = "sura-$suraIndex"
                val suraTafsir = tafsirData[suraKey]
                val suraIdForAudio = surah?.index ?: "0"

                if (showTranslation) {
                    // Режим перевода: Список аятов
                    currentSuraData?.aya?.forEach { aya ->
                        val indexStr = aya.attributes?.index ?: ""

                        // Отображение маркера Джуза
                        juzMarkers[indexStr]?.let { marker ->
                            JuzHeader(marker)
                        }

                        val text = aya.attributes?.text ?: ""
                        val ayahTafsir = suraTafsir?.get("ayah-$indexStr")
                        val translation = ayahTafsir?.tafsir
                        val note = ayahTafsir?.note

                        AyahBlock(
                            index = indexStr,
                            arabicText = text,
                            translationText = translation,
                            noteText = note,
                            onPlayClick = {
                                translation?.let {
                                    playTtsAudio(context, suraIdForAudio, indexStr, it, coroutineScope, mediaPlayer)
                                }
                            }
                        )
                    }
                } else {
                    // Режим чтения книги: Сплошной арабский текст
                    val annotatedString = buildAnnotatedString {
                        currentSuraData?.aya?.forEach { aya ->
                            val indexStr = aya.attributes?.index ?: ""

                            // Маркер Джуза в режиме чтения
                            juzMarkers[indexStr]?.let { marker ->
                                val label = when(marker) {
                                    is JuzMarker.Start -> "— Җөз ${marker.index} (башы) —"
                                    is JuzMarker.Continuation -> "— Җөз ${marker.index} (дәвамы) —"
                                }
                                withStyle(style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 18.sp
                                )) {
                                    append("\n\n$label\n\n")
                                }
                            }

                            val text = aya.attributes?.text ?: ""

                            withStyle(style = SpanStyle(fontFamily = fontFamily, fontSize = 26.sp)) {
                                append(text)
                                append(" ")
                            }

                            withStyle(style = SpanStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)) {
                                append("($indexStr) ")
                            }
                        }
                    }

                    Text(
                        text = annotatedString,
                        lineHeight = 54.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun JuzHeader(marker: JuzMarker) {
    val text = when (marker) {
        is JuzMarker.Start -> "Җөз ${marker.index} (башы)"
        is JuzMarker.Continuation -> "Җөз ${marker.index} (дәвамы)"
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(
                if (marker is JuzMarker.Start) MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.secondaryContainer, 
                MaterialTheme.shapes.small
            )
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (marker is JuzMarker.Start) MaterialTheme.colorScheme.onPrimaryContainer 
                    else MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AyahBlock(
    index: String,
    arabicText: String,
    translationText: String?,
    noteText: String?,
    onPlayClick: () -> Unit
) {
    var showNote by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        // Арабский текст (справа)
        Text(
            text = arabicText,
            fontFamily = fontFamily,
            fontSize = 26.sp,
            lineHeight = 40.sp,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Перевод и кнопка воспроизведения
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "($index) ${translationText ?: ""}",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            Row {
                if (!noteText.isNullOrBlank()) {
                    IconButton(onClick = { showNote = !showNote }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Show Note",
                            tint = if (showNote) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline
                        )
                    }
                }

                if (translationText != null) {
                    IconButton(onClick = onPlayClick) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play Translation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        if (showNote && !noteText.isNullOrBlank()) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = noteText,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 0.5.dp)
    }
}

private fun playTtsAudio(
    context: Context,
    suraIndex: String,
    ayahIndex: String,
    text: String,
    scope: CoroutineScope,
    mediaPlayer: MediaPlayer
) {
    if (text.isBlank()) return

    val audioDir = File(context.filesDir, "audio_quran")
    if (!audioDir.exists()) audioDir.mkdirs()
    val audioFile = File(audioDir, "audio_${suraIndex}_${ayahIndex}.mp3")

    if (audioFile.exists() && audioFile.length() > 0) {
        playLocalFile(audioFile, mediaPlayer)
        return
    }

    scope.launch(Dispatchers.IO) {
        try {
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val urlString = "https://tat-tts.api.translate.tatar/listening/?speaker=almaz&text=$encodedText"

            val response = URL(urlString).readText().trim()
            val base64Str = json.decodeFromString<JsonObject>(response).getValue("wav_base64").toString()
            val cleanBase64 = base64Str.removeSurrounding("\"")

            val audioBytes = Base64.decode(cleanBase64, Base64.DEFAULT)

            FileOutputStream(audioFile).use { it.write(audioBytes) }

            withContext(Dispatchers.Main) {
                playLocalFile(audioFile, mediaPlayer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun playLocalFile(file: File, mediaPlayer: MediaPlayer) {
    try {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.reset()
        mediaPlayer.setDataSource(file.absolutePath)
        mediaPlayer.prepare()
        mediaPlayer.start()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
