package ru.softmine.simplenotes.ui.splash

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.errors.NoAuthException
import ru.softmine.simplenotes.ui.base.BaseViewModel

@ExperimentalCoroutinesApi
class SplashViewModel(private val repository: Repository) :
    BaseViewModel<Boolean?>() {

    fun requestUser() {
        launch {
            repository.getCurrentUser()?.let {
                setData(true)
            } ?: setError(NoAuthException())
        }
    }
}