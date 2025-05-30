package com.example.composeapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import com.example.composeapp.data.enitities.suralist.SurahItem
import com.example.composeapp.ui.theme.QuranAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val str = Json.decodeFromStream<List<SurahItem>>(assets.open("suras.json"))
        setContent {
//            QuranAppTheme {
//                MyNavHost(suraList = str)
//            }
            val scope = rememberCoroutineScope()
            QuranAppTheme {
                var offset by remember {
                    mutableStateOf(
                        listOf<Pair<Color, Path>>(
                            Pair(Color.Red, Path()),
                        )
                    )
                }
                var pt by remember { mutableStateOf(Offset(0f, 0f)) }
                var prev: PointerEventType? by remember { mutableStateOf(null) }
                var needBmp by remember { mutableStateOf(false) }
                val picture = remember { Picture() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                modifier = Modifier.weight(0.1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.Red),
                                onClick = {
                                    offset = offset.toMutableList().apply {
                                        add(Pair(Color.Red, Path()))
                                    }
                                },
                            ) {

                            }
                            Button(
                                modifier = Modifier.weight(0.1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.Blue),
                                onClick = {
                                    offset = offset.toMutableList().apply {
                                        add(Pair(Color.Blue, Path()))
                                    }
                                },
                            ) {

                            }
                            Button(
                                modifier = Modifier.weight(0.1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.Green),
                                onClick = {
                                    offset = offset.toMutableList().apply {
                                        add(Pair(Color.Green, Path()))
                                    }
                                },
                            ) {

                            }
                            Button(
                                modifier = Modifier.weight(0.1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.Black),
                                onClick = {
                                    offset = offset.toMutableList().apply {
                                        add(Pair(Color.Black, Path()))
                                    }
                                },
                            ) {

                            }
                            Button(
                                modifier = Modifier.weight(0.1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.Yellow),
                                onClick = {
                                    offset = offset.toMutableList().apply {
                                        add(Pair(Color.Yellow, Path()))
                                    }
                                },
                            ) {

                            }
                            Button(
                                modifier = Modifier.weight(0.1f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.Gray),
                                onClick = {
                                    offset = offset.toMutableList().apply {
                                        add(Pair(Color.Gray, Path()))
                                    }
                                },
                            ) {

                            }
                            Button(
                                modifier = Modifier.weight(0.2f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.White),
                                onClick = {
                                    offset = listOf(Pair(Color.Red, Path()))
                                },
                            ) {
                                Image(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                )
                            }
                            Button(
                                modifier = Modifier.weight(0.2f),
                                shape = RectangleShape,
                                colors = ButtonDefaults.buttonColors()
                                    .copy(containerColor = Color.White),
                                onClick = {
                                    needBmp = true
                                },
                            ) {
                                Image(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = null,
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .drawWithCache {
                                    pt

                                    onDrawWithContent {
                                        offset.forEach {
                                            drawPath(
                                                color = it.first,
                                                path = it.second,
                                                style = Stroke(width = 10f)
                                            )
                                        }

                                        if (needBmp) {
                                            val width = size.width.toInt()
                                            val height = size.height.toInt()

                                            saveToPicture(picture, width, height, offset, scope)

                                            needBmp = false
                                        }
                                    }
                                }
                                .pointerInput("key") {
                                    awaitPointerEventScope {
                                        while (true) {
                                            val event = awaitPointerEvent()
                                            println(event.type)
                                            pt = event.changes.getOrNull(0)?.position ?: Offset(
                                                0f,
                                                0f
                                            )
                                            val offset = offset.last()
                                            println(event.changes.size)
                                            if (event.type == PointerEventType.Press) {
                                                event.changes.forEach {
                                                    offset.second.moveTo(
                                                        it.position.x,
                                                        it.position.y
                                                    )
                                                    offset.second.lineTo(
                                                        it.position.x,
                                                        it.position.y
                                                    )
                                                }
                                            } else if (event.type == PointerEventType.Move) {
                                                event.changes.forEach {
                                                    offset.second.lineTo(
                                                        it.position.x,
                                                        it.position.y
                                                    )
                                                }
                                            } else if (event.type == PointerEventType.Release) {
                                                event.changes.forEach {
                                                    offset.second.moveTo(
                                                        it.position.x,
                                                        it.position.y
                                                    )
                                                    if (prev == PointerEventType.Press) {
                                                        offset.second.lineTo(
                                                            it.position.x + 10,
                                                            it.position.y + 10
                                                        )
                                                        pt =
                                                            (event.changes.getOrNull(0)?.position
                                                                ?: Offset(
                                                                    0f,
                                                                    0f
                                                                )).copy(
                                                                x = it.position.x + 5,
                                                                y = it.position.y + 5,
                                                            )
                                                    }
                                                }
                                            }
                                            prev = event.type
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }

    private fun saveToPicture(
        picture: Picture,
        width: Int,
        height: Int,
        offset: List<Pair<Color, Path>>,
        scope: CoroutineScope
    ) {
        Canvas(picture.beginRecording(width, height)).apply {
            offset.forEach {
                drawPath(
                    paint = Paint().apply {
                        color = it.first
                        strokeWidth = 10f
                        style = PaintingStyle.Stroke
                    },
                    path = it.second,
                )
            }
        }

        picture.endRecording()

        scope.launch {
            val bitmap = createBitmapFromPicture(picture)
            bitmap.saveToDisk(this@MainActivity)
        }
    }

    private fun createBitmapFromPicture(picture: Picture): Bitmap {
        val bitmap = Bitmap.createBitmap(
            picture.width,
            picture.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.WHITE)
        canvas.drawPicture(picture)
        return bitmap
    }

    private suspend fun Bitmap.saveToDisk(context: Context): Uri {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "screenshot-${System.currentTimeMillis()}.png"
        )

        file.writeBitmap(this, Bitmap.CompressFormat.PNG, 100)

        return scanFilePath(context, file.path) ?: throw Exception("File could not be saved")
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

    private suspend fun scanFilePath(context: Context, filePath: String): Uri? {
        return suspendCancellableCoroutine { continuation ->
            MediaScannerConnection.scanFile(
                context,
                arrayOf(filePath),
                arrayOf("image/png")
            ) { _, scannedUri ->
                if (scannedUri == null) {
                    continuation.cancel(Exception("File $filePath could not be scanned"))
                } else {
                    continuation.resume(scannedUri) {}
                }
            }
        }
    }
}

/*
* https://tat-tts.api.translate.tatar/listening/?speaker=almaz&text=алты+ике+дурт
* val str = Json.decodeFromStream<QuranData>(this.assets.open("quran.json"))

* */