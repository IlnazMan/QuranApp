package ru.umma.ummaruapp.view.surah

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.l4digital.fastscroll.FastScrollRecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.data.models.Surah

class SurahActivity : AppCompatActivity() {
    private val surah by lazy {
        intent.getSerializableExtra(KEY_SURAH) as Surah
    }

    private val _viewModel by viewModel<SurahViewModel> {
        parametersOf(surah)
    }

    private val _list by lazy {
        findViewById<RecyclerView>(R.id.list)
    }

    private val adapter: SurahAdapter by lazy {
        SurahAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _list.adapter = adapter
        _list.addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val pos = (_list.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                adapter.currentList.getOrNull(pos)?.let {
                    _viewModel.saveLastAyah(it.number)
                }
            }
        })
        (_list as FastScrollRecyclerView)
        title = surah.name
        _viewModel.surahListLiveData.observe(this) { (data, lastAyah) ->
            adapter.submitList(data)
            adapter.currentList.indexOfFirst { it.number == lastAyah }.takeIf { it > 0 }?.let {
                _list.scrollToPosition(it)
            }
        }
    }

    companion object {
        fun newIntent(context: Context, surah: Surah): Intent =
            Intent(context, SurahActivity::class.java).apply {
                putExtra(KEY_SURAH, surah)
            }

        private const val KEY_SURAH = "surah"
    }
}