package ru.softmine.simplenotes.ui.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ru.softmine.simplenotes.R

abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {

    abstract val viewModel: BaseViewModel<T, S>
    abstract val layoutRes: Int
    //abstract val mainRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        viewModel.getViewState().observe(this, object : Observer<S> {
            override fun onChanged(t: S?) {
                if (t == null) return

                if (t.data != null) {
                    renderData(t.data)
                }

                if (t.error != null) {
                    renderError(t.error)
                }
            }
        })
    }

    abstract fun renderData(data: T)

    protected fun renderError(error: Throwable) {
        if (error.message != null) {
            showError(error.message!!)
        }
    }

    protected fun showError(error: String) {
//        val snackbar = Snackbar.make(view, error, Snackbar.LENGTH_INDEFINITE)
//        snackbar.setAction(R.string.ok_button, View.OnClickListener {
//            snackbar.dismiss()
//        })
//        snackbar.show()
    }
}
