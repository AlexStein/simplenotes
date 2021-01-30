package ru.softmine.simplenotes.ui.splash

import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.errors.NoAuthException
import ru.softmine.simplenotes.ui.base.BaseViewModel

class SplashViewModel(private val repository: Repository = Repository) :
    BaseViewModel<Boolean?, SplashViewState>() {

    fun requestUser() {
        repository.getCurrentUser().observeForever {
            viewStateLiveData.value = it?.let {
                SplashViewState(isAuth = true)
            } ?: SplashViewState(error = NoAuthException())
        }
    }
}