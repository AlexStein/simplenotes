package ru.softmine.simplenotes.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.data.errors.NoAuthException

private const val RC_SIGN_IN = 458

abstract class BaseActivity<T, S : BaseViewState<T>> : AppCompatActivity() {

    abstract val model: BaseViewModel<T, S>
    abstract val layoutRes: Int
    abstract val ui: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)

        model.getViewState().observe(this, { t ->
            t?.apply {
                data?.let { renderData(it) }
                error?.let { renderError(it) }
            }
        })
    }

    abstract fun renderData(data: T)

    protected open fun renderError(error: Throwable) {
        when (error) {
            is NoAuthException -> startLoginActivity()
            else -> error.message?.let { showError(it) }
        }
    }

    protected open fun showError(error: String) {
        Snackbar.make(ui.root, error, Snackbar.LENGTH_INDEFINITE).apply {
            this.setAction(R.string.ok_button) { this.dismiss() }
            this.show()
        }
    }

    private fun startLoginActivity() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLogo(R.drawable.common_full_open_on_phone)
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN && resultCode != Activity.RESULT_OK) {
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
