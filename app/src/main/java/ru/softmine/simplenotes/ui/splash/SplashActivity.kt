package ru.softmine.simplenotes.ui.splash

import android.os.Handler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.databinding.ActivitySplashBinding
import ru.softmine.simplenotes.ui.base.BaseActivity
import ru.softmine.simplenotes.ui.main.MainActivity

private const val START_DELAY = 1000L

@ExperimentalCoroutinesApi
class SplashActivity : BaseActivity<Boolean?>() {

    override val model: SplashViewModel by viewModel()
    override val layoutRes: Int = R.layout.activity_splash
    override val ui: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ model.requestUser() }, START_DELAY)
    }

    override fun renderData(data: Boolean?) {
        data?.takeIf { it }?.let { startMainActivity() }
    }

    private fun startMainActivity() {
        startActivity(MainActivity.getStartIntent(this))
        finish()
    }
}