package ru.umma.ummaruapp.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.view.surah.SurahActivity

class MainActivity : AppCompatActivity() {
    private val _adapter: MainAdapter by lazy {
        MainAdapter {
            startActivity(SurahActivity.newIntent(this, it))
        }
    }
    private val _viewModel by viewModel<MainViewModel>()
    private val _last by lazy { findViewById<MaterialButton>(R.id.last_ayah) }
    private val _list by lazy { findViewById<RecyclerView>(R.id.list) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _list.apply {
            adapter = _adapter
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        _viewModel.surahListLiveData.observe(this) { (list, lastAya) ->
            _adapter.submitList(list)
            _last.isVisible = lastAya != null
            lastAya?.let {
                val suraNmb = lastAya.split(":").getOrNull(0)
                _last.setOnClickListener {
                    list.find { it.number == suraNmb }?.let {
                        startActivity(SurahActivity.newIntent(this, it))
                    }
                }
                val toScroll = list.indexOfFirst { it.number == suraNmb }
                _list.scrollToPosition(toScroll)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        _viewModel.checkLastAyah()
    }
}