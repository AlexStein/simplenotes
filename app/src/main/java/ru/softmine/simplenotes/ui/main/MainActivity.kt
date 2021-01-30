package ru.softmine.simplenotes.ui.main

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ActivityMainBinding
import ru.softmine.simplenotes.ui.base.BaseActivity
import ru.softmine.simplenotes.ui.note.NoteActivity

class MainActivity : BaseActivity<List<Note>?, MainViewState>() {

    override val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }
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

    private fun openNoteActivity(note: Note?) {
        val intent = NoteActivity.getNoteStartIntent(this, note?.id)
        startActivity(intent)
    }

    override fun renderData(data: List<Note>?) {
        data?.let { adapter.notes = it }
    }
}