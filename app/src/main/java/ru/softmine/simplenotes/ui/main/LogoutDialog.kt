package ru.softmine.simplenotes.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.softmine.simplenotes.R

class LogoutDialog : DialogFragment() {
    companion object {
        var TAG = LogoutDialog::class.java.name + "TAG"
        fun createInstance() = LogoutDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(this.context)
            .setTitle(R.string.logout_title)
            .setMessage(R.string.logout_message)
            .setPositiveButton(R.string.ok_button) { _, _ -> (activity as LogoutListener).onLogout() }
            .setNegativeButton(R.string.cancel_button) { _, _ -> dismiss() }
            .create()

    interface LogoutListener {
        fun onLogout()
    }
}