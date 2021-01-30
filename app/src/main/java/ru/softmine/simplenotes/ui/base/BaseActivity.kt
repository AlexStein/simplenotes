package ru.softmine.simplenotes.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import ru.softmine.simplenotes.R

abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {

    abstract val viewModel: BaseViewModel<T, S>
    abstract val layoutRes: Int
    abstract val ui: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        viewModel.getViewState().observe(this, { t ->
            t?.apply {
                data?.let { renderData(it) }
                error?.let { renderError(it) }
            }
        })
    }

    abstract fun renderData(data: T)

    protected fun renderError(error: Throwable) {
        error.message?.let { showError(it) }
    }

    protected open fun showError(error: String) {
        Snackbar.make(ui.root, error, Snackbar.LENGTH_INDEFINITE).apply {
            this.setAction(R.string.ok_button) { this.dismiss() }
            this.show()
        }
    }
}
