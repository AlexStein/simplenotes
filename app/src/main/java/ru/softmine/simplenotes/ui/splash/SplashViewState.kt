package ru.softmine.simplenotes.ui.splash

import ru.softmine.simplenotes.ui.base.BaseViewState

class SplashViewState(isAuth: Boolean? = null, error: Throwable? = null) :
    BaseViewState<Boolean?>(isAuth, error)