package ru.umma.ummaruapp.view.search

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.view.surah.SurahActivity

class SearchResultsActivity : AppCompatActivity() {
    private val _viewModel by viewModel<SearchViewModel>()

    private val _searchText by lazy { intent.getStringExtra(SearchManager.QUERY) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val backIcon =
            getDrawable(com.google.android.material.R.drawable.material_ic_keyboard_arrow_left_black_24dp)
        backIcon?.setTint(resources.getColor(R.color.text_color_primary))

        supportActionBar?.apply {
            setHomeAsUpIndicator(backIcon)
            setDisplayHomeAsUpEnabled(true)
        }
        setContent {
            val state by _viewModel.surahListLiveData.observeAsState()
            when (val data = state) {
                is SearchResult.Empty -> {
                    Box(
                        Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        Text(
                            "Не найдено",
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                letterSpacing = 1.sp,
                                fontSize = 18.sp,
                                lineHeight = 24.sp,
                                textAlign = TextAlign.Center,
                            ),
                            color = MaterialTheme.colors.primary,
                        )
                    }
                }

                is SearchResult.Data -> {
                    LazyColumn {
                        item {
                            Text(
                                modifier = Modifier.padding(8.dp),
                                text = "Для текста \"$_searchText\" найдено: ${data.result.size} аятов",
                                fontSize = 20.sp,
                            )
                        }
                        items(data.result) { (ayahBlock, surah) ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        startActivity(
                                            SurahActivity.newIntent(
                                                this@SearchResultsActivity,
                                                surah = surah,
                                                savedAyah = ayahBlock.number to 0,
                                                needScroll = true,
                                            )
                                        )
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = ayahBlock.number,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                                Text(
                                    text = ayahBlock.translate,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Justify,
                                    style = TextStyle(
                                        letterSpacing = 1.sp,
                                        fontSize = 18.sp,
                                        lineHeight = 24.sp,
                                    ),
                                    color = MaterialTheme.colors.primary,
                                )
                            }
                        }
                    }
                }

                else -> {
                    Box(
                        Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }

        }
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            lifecycleScope.launch {
                _viewModel.findAyas(_searchText.orEmpty())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }
}