package ru.softmine.simplenotes.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.ext.android.viewModel
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ActivityMainBinding
import ru.softmine.simplenotes.ui.base.BaseActivity
import ru.softmine.simplenotes.ui.note.NoteActivity
import ru.softmine.simplenotes.ui.splash.SplashActivity

@ExperimentalCoroutinesApi
class MainActivity : BaseActivity<List<Note>?>(), LogoutDialog.LogoutListener {

    companion object {
        fun getStartIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override val model: MainViewModel by viewModel()
    override val layoutRes = R.layout.activity_main
    override val ui: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)

        adapter = MainAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteActivity(note)
            }
        })

        ui.mainRecycler.adapter = adapter
        ui.fab.setOnClickListener { openNoteActivity(null) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        MenuInflater(this).inflate(R.menu.menu_main, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.logout -> showLogoutDialog().let { true }
            else -> false
        }

    private fun showLogoutDialog() {
        supportFragmentManager.findFragmentByTag(LogoutDialog.TAG) ?: LogoutDialog.createInstance()
            .show(supportFragmentManager, LogoutDialog.TAG)
    }

    private fun openNoteActivity(note: Note?) {
        val intent = NoteActivity.getNoteStartIntent(this, note?.id)
        startActivity(intent)
    }

    override fun renderData(data: List<Note>?) {
        data?.let { adapter.notes = it }
    }

    override fun onLogout() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                startActivity(Intent(this, SplashActivity::class.java))
                finish()
            }
    }
}