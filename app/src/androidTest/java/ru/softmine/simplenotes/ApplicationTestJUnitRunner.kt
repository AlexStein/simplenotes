package ru.softmine.simplenotes

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class ApplicationTestJUnitRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, ApplicationTest::class.java.name, context)
    }

}