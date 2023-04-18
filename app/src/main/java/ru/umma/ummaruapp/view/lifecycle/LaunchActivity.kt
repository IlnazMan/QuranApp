package ru.umma.ummaruapp.view.lifecycle

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.umma.ummaruapp.R
import ru.umma.ummaruapp.view.main.MainActivity

class LaunchActivity : AppCompatActivity() {
    private val _viewModel by viewModel<LaunchViewModel>()
    private val _progress: ProgressBar by lazy {
        findViewById(R.id.progressBar)
    }
    private val _retry: MaterialButton by lazy {
        findViewById(R.id.retry)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        _viewModel.dataIsReady.observe(this) {
            if (it) {
                lifecycleScope.launch {
                    delay(1_000)
                    startActivity(Intent(this@LaunchActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
        _viewModel.progress.observe(this) {
            _progress.isVisible = it.first > 0
            _progress.max = it.second
            _progress.progress = it.first
        }
        _viewModel.error.observe(this) {
            _retry.isVisible = it != null
            _progress.isVisible = false
            it?.let {
                Toast.makeText(this, it, Snackbar.LENGTH_SHORT).show()
            }
        }
        _retry.setOnClickListener {
            _viewModel.retry()
        }
    }
}