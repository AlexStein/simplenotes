package ru.softmine.simplenotes.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ActivityMainBinding
import ru.softmine.simplenotes.ui.note.NoteActivity

class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding
    private lateinit var adapter: MainAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        adapter = MainAdapter(object : OnItemClickListener {
            override fun onItemClick(note: Note) {
                openNoteActivity(note)
            }
        })

        ui.mainRecycler.adapter = adapter
        ui.fab.setOnClickListener { openNoteActivity(null) }

        viewModel.viewState().observe(this, Observer<MainViewState> { t ->
            t?.let { adapter.notes = it.notes }
        })
    }

    private fun openNoteActivity(note: Note?) {
        val intent = NoteActivity.getNoteStartIntent(this, note)
        startActivity(intent)
    }
}