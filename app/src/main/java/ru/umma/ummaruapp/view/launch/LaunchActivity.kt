package ru.umma.ummaruapp.view.launch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.view.base.fontFamily
import ru.umma.ummaruapp.view.main.MainActivity
import ru.umma.ummaruapp.view.theme.UmmaTheme

class LaunchActivity : AppCompatActivity() {
    private val _viewModel by viewModel<LaunchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by _viewModel.state.observeAsState()
            UmmaTheme {
                LaunchScreen(state,
                    onDataReady = {
                        lifecycleScope.launch {
                            delay(1_000)
                            startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                            finish()
                        }
                    },
                    onError = {
                        Toast.makeText(this@LaunchActivity, it, Toast.LENGTH_SHORT).show()
                    },
                    onRetry = { _viewModel.retry() })
            }
        }
    }


}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LaunchScreen(
    state: State? = State(progress = 10 to 100),
    onRetry: () -> Unit = {},
    onDataReady: () -> Unit = {},
    onError: (String) -> Unit = {},
) {
    if (state == null) return

    if (state.dataIsReady) {
        onDataReady()
        return
    }

    val error = state.error

    if (error != null) {
        onError(error)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp, 60.dp)
                    .background(color = Color(0x7CE9E9E9), shape = CircleShape)
                    .clip(CircleShape),
            )
            Text(
                stringResource(id = R.string.text_tafsir),
                fontFamily = fontFamily,
                fontSize = 24.sp
            )
        }
        val pr = state.progress.first
        val max = state.progress.second


        val progress by animateFloatAsState(
            targetValue = if (max > 0) {
                (pr.toFloat() / max)
            } else 0f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            if (error != null) {
                Button(
                    onClick = onRetry,
                ) {
                    Text(text = stringResource(id = R.string.text_retry))
                }
            }

            if (progress > 0 && error == null) {
                LinearProgressIndicator(
                    progress = progress,
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(4.dp),
                    color = MaterialTheme.colors.primary,
                    backgroundColor = MaterialTheme.colors.secondary
                )
            }
        }
    }
}

@Preview
@Composable
fun LaunchScreenPreview() {
    UmmaTheme() {
        LaunchScreen()
    }
}

