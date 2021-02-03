package ru.softmine.simplenotes

import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.softmine.simplenotes.di.appModule
import ru.softmine.simplenotes.di.mainModule
import ru.softmine.simplenotes.di.noteModule
import ru.softmine.simplenotes.di.splashModule

class Application : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@Application)
            modules(appModule, splashModule, mainModule, noteModule)
        }
    }
}