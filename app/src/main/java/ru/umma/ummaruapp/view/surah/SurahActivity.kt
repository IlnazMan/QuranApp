package ru.umma.ummaruapp.view.surah

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.NightMode
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Colors
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.data.models.AyahBlock
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.view.base.FastScroller
import ru.umma.ummaruapp.view.base.fontFamily
import ru.umma.ummaruapp.view.theme.UmmaTheme

class SurahActivity : AppCompatActivity() {
    private val surah by lazy {
        intent.getSerializableExtra(KEY_SURAH) as Surah
    }

    private val needInitialScroll by lazy {
        intent.getBooleanExtra(KEY_NEED_INITIAL_SCROLL, false)
    }

    private val _viewModel by viewModel<SurahViewModel> {
        parametersOf(surah)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by _viewModel.surahListLiveData.observeAsState()
            val listState = rememberLazyListState()
            var initialScroll by rememberSaveable {
                mutableStateOf(!needInitialScroll)
            }

            UmmaTheme {
                SurahScreen(
                    screenState = state,
                    listState = listState,
                    savePos = { aya, offset ->
                        println("!!! $aya")
                        state?.surah?.getOrNull(aya)?.let {
                            _viewModel.saveLastAyah(it.number, offset)
                            println("!!! ${it.number}")
                        }
                    },
                    needScroll = initialScroll,
                    updateScroll = {
                        initialScroll = true
                    }
                )
            }
        }
        title = surah.name
        val backIcon =
            getDrawable(com.google.android.material.R.drawable.material_ic_keyboard_arrow_left_black_24dp)
        backIcon?.setTint(resources.getColor(R.color.text_color_primary))

        supportActionBar?.apply {
            setHomeAsUpIndicator(backIcon)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    companion object {
        fun newIntent(
            context: Context,
            surah: Surah,
            needScroll: Boolean = false
        ): Intent =
            Intent(context, SurahActivity::class.java).apply {
                putExtra(KEY_SURAH, surah)
                putExtra(KEY_NEED_INITIAL_SCROLL, needScroll)
            }

        private const val KEY_SURAH = "surah"
        private const val KEY_NEED_INITIAL_SCROLL = "scroll"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}

@Composable
fun SurahScreen(
    screenState: SuraScreenState?,
    modifier: Modifier = Modifier,
    listState: LazyListState,
    savePos: (Int, Int) -> Unit,
    needScroll: Boolean,
    updateScroll: () -> Unit
) {
    val scope = rememberCoroutineScope()

    screenState?.let { st ->
        val data = st.surah

        Box(modifier = modifier.background(MaterialTheme.colors.surface)) {
            LazyColumn(
                modifier = modifier
                    .padding(horizontal = 16.dp),
                state = listState
            ) {
                itemsIndexed(data) { idx, item ->
                    var itemInfoState by rememberSaveable {
                        mutableStateOf(idx to false)
                    }

                    AyahBlockItem(
                        ayahBlock = item,
                        itemInfoState = itemInfoState,
                    ) {
                        itemInfoState = idx to it
                    }
                }
            }
        }

        if (!needScroll) {
            scope.launch {
                val idx = data.indexOfFirst { it.number == st.lastAyah }.takeIf { it >= 0 } ?: 0

                listState.scrollToItem(idx)
                listState.animateScrollBy(st.offsetInAyah.toFloat())
                updateScroll()
            }
        } else {
            savePos(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset)
        }
    }
}

@Composable
fun AyahBlockItem(
    ayahBlock: AyahBlock,
    modifier: Modifier = Modifier,
    itemInfoState: Pair<Int, Boolean>,
    onIconClick: (Boolean) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = ayahBlock.number,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = Color.Gray,
            modifier = modifier.padding(vertical = 16.dp)
        )
        Text(
            text = ayahBlock.arabic,
            fontFamily = fontFamily,
            textAlign = TextAlign.Justify,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            fontSize = 24.sp,
            style = TextStyle(
                textDirection = TextDirection.Rtl
            ),
            color = MaterialTheme.colors.onPrimary,
        )
        Text(
            text = ayahBlock.transcription,
            style = TextStyle(
                letterSpacing = 1.sp,
                fontSize = 16.sp,
                color = Color.Gray
            ),
            textAlign = TextAlign.Justify,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )
        Text(
            text = ayahBlock.translate,
            modifier = modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Justify,
            style = TextStyle(
                letterSpacing = 1.sp,
                fontSize = 18.sp,
                lineHeight = 24.sp,
            ),
            color = MaterialTheme.colors.onPrimary,
        )

        if (ayahBlock.explanation.isNotEmpty()) {
            AnimatedVisibility(visible = itemInfoState.second) {
                Text(
                    text = ayahBlock.explanation,
                    modifier = modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            onIconClick(false)
                        },
                    style = TextStyle(
                        color = Color.Gray
                    ),
                )
            }
            AnimatedVisibility(visible = !itemInfoState.second) {
                IconButton(onClick = { onIconClick(true) }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = modifier.padding(vertical = 16.dp))
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AyahBlockItemPreview() {
    UmmaTheme {
        SurahScreen(
            screenState = SuraScreenState(
                surah = listOf(
                    AyahBlock(
                        "10:10",
                        "text",
                        "text",
                        "text",
                        "text",
                    )
                ),
                null,
                offsetInAyah = 0,
            ), listState = LazyListState(), savePos = { _, _ -> }, needScroll = false
        ) {

        }
    }
}
