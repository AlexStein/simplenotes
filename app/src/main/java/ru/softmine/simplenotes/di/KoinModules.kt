package ru.softmine.simplenotes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.provider.FirebaseRemoteProvider
import ru.softmine.simplenotes.data.provider.RemoteDataProvider
import ru.softmine.simplenotes.ui.main.MainViewModel
import ru.softmine.simplenotes.ui.note.NoteViewModel
import ru.softmine.simplenotes.ui.splash.SplashViewModel

val appModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseRemoteProvider(get(), get()) } bind RemoteDataProvider::class
    single { Repository(get()) }
}

val splashModule = module {
    viewModel { SplashViewModel(get()) }
}

val mainModule = module {
    viewModel { MainViewModel(get()) }
}

val noteModule = module {
    viewModel { NoteViewModel(get()) }
}
