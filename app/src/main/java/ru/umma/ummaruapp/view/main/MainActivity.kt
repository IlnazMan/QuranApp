package ru.umma.ummaruapp.view.main

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.view.Menu
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.data.models.Surah
import ru.umma.ummaruapp.view.search.SearchResultsActivity
import ru.umma.ummaruapp.view.surah.SurahActivity
import ru.umma.ummaruapp.view.theme.UmmaTheme

class MainActivity : AppCompatActivity() {
    private val _viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val data by _viewModel.surahListLiveData.observeAsState()
            val lastRead = data?.let { it.last.orEmpty() to it.lastOffset }
            val suraNmb = data?.last?.split("-")?.getOrNull(0)
            val listState = rememberLazyListState()

            SuraListScreen(
                state = data,
                onLastCLick = {
                    data?.suraList?.find { it.number == suraNmb }?.let {
                        startActivity(
                            SurahActivity.newIntent(
                                this@MainActivity,
                                surah = it,
                                savedAyah = lastRead,
                                needScroll = true,
                            )
                        )
                    }
                },
                onSuraClick = {
                    startActivity(
                        SurahActivity.newIntent(
                            this@MainActivity,
                            surah = it,
                            savedAyah = null
                        )
                    )
                },
                listState = listState
            )
        }
    }

    override fun onResume() {
        super.onResume()
        _viewModel.updateData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val component = ComponentName(this, SearchResultsActivity::class.java)
        val searchableInfo = searchManager.getSearchableInfo(component)
        searchView.setSearchableInfo(searchableInfo)
        return true
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
    val screenHeight by remember {
        derivedStateOf { listState.layoutInfo.viewportSize.height }
    }
    val itemsCount by remember {
        derivedStateOf { listState.layoutInfo.totalItemsCount }
    }
    var color by remember { mutableStateOf(Color.Transparent) }
    UmmaTheme {
        Box(modifier = modifier) {
            Column {
                if (state?.last != null) {
                    Button(
                        modifier = Modifier
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
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(40.dp)
                    .background(color)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            val pos =
                                (change.position.y / screenHeight) * itemsCount
                            scope.launch {
                                if (pos > 0 && pos < itemsCount)
                                    listState.scrollToItem(pos.toInt())
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        detectTapGestures { change ->
                            val pos =
                                (change.y / screenHeight) * itemsCount
                            scope.launch {
                                if (pos > 0 && pos < itemsCount)
                                    listState.scrollToItem(pos.toInt())
                            }
                        }
                    }
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                val event = awaitPointerEvent()
                                // handle pointer event
                                if (event.type == PointerEventType.Move || event.type == PointerEventType.Press) {
                                    color = Color.LightGray.copy(alpha = 0.1f)
                                } else if (event.type == PointerEventType.Release) {
                                    color = Color.Transparent
                                }
                            }
                        }
                    }
            )
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
