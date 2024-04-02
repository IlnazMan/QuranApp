package ru.umma.ummaruapp.view.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.view.base.FastScroller
import ru.umma.ummaruapp.view.surah.SurahActivity
import ru.umma.ummaruapp.view.theme.UmmaTheme

class MainActivity : AppCompatActivity() {
    private val _viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val data by _viewModel.surahListLiveData.observeAsState()
            val suraNmb = data?.last?.split(":")?.getOrNull(0)
            val listState = rememberLazyListState()

            SuraListScreen(
                state = data,
                onLastCLick = {
                    data?.suraList?.find { it.number == suraNmb }?.let {
                        startActivity(SurahActivity.newIntent(this@MainActivity, it, true))
                    }
                },
                onSuraClick = {
                    startActivity(SurahActivity.newIntent(this@MainActivity, it))
                },
                listState = listState
            )
        }
    }

    override fun onResume() {
        super.onResume()
        _viewModel.updateData()
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SuraListScreen(
    modifier: Modifier = Modifier,
    onLastCLick: () -> Unit = {},
    onSuraClick: (Surah) -> Unit = {},
    state: MainScreenState? = null,
    listState: LazyListState,
    scope: CoroutineScope = rememberCoroutineScope(),
) {

    UmmaTheme {
        Box(modifier = modifier) {
            Column {
                if (state?.last != null) {
                    Button(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp),
                        onClick = onLastCLick,
                        shape = RectangleShape,
                        colors = ButtonDefaults.buttonColors()
                    ) {
                        Text(
                            text = stringResource(id = R.string.title_last_read),
                        )
                    }
                }
                LazyColumn(state = listState) {
                    items(state?.suraList.orEmpty()) {
                        SurahItem(surah = it) {
                            onSuraClick(it)
                        }
                    }
                }
            }

            val index by remember { derivedStateOf { listState.firstVisibleItemIndex } }
            val count by remember { derivedStateOf { listState.layoutInfo.totalItemsCount } }
            val visibleSize by remember { derivedStateOf { listState.layoutInfo.visibleItemsInfo.size } }

            FastScroller(
                progress = index.toFloat(),
                modifier = modifier,
                maxValue = count,
            ) { position ->
                scope.launch {
                    val scroll =
                        position + ((position + visibleSize) /
                                (count.takeIf { it > 0 } ?: 1))
                    listState.scrollToItem(scroll.toInt())
                }
            }
        }
    }
}

@Composable
fun SurahItem(
    surah: Surah,
    modifier: Modifier = Modifier,
    onSuraClick: (Surah) -> Unit,
) {
    Surface(color = MaterialTheme.colors.surface) {
        Row(
            modifier = modifier
                .height(64.dp)
                .clickable {
                    onSuraClick(surah)
                }
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = modifier
                    .padding(horizontal = 16.dp),
                text = surah.number,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = surah.name,
                Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
        }
    }
}
