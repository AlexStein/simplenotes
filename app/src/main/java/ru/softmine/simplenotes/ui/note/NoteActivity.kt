package ru.softmine.simplenotes.ui.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.common.getColorResource
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ActivityNoteBinding
import ru.softmine.simplenotes.ui.base.BaseActivity
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<Note?, NoteViewState>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"

        fun getNoteStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    override val viewModel: NoteViewModel by lazy {
        ViewModelProvider(this).get(NoteViewModel::class.java)
    }
    override val layoutRes = R.layout.activity_note

    private lateinit var ui: ActivityNoteBinding
    private var note: Note? = null


    private val textChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // not needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // not needed
        }

        override fun afterTextChanged(s: Editable?) {
            triggerSaveNote()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getStringExtra(EXTRA_NOTE)
        ui = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(ui.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId?.let {
            viewModel.loadNote(it)
        }

        if (noteId == null) {
            supportActionBar?.title = getString(R.string.new_note_title)
        }

        ui.noteTitleEdit.addTextChangedListener(textChangeListener)
        ui.noteBodyEdit.addTextChangedListener(textChangeListener)

        initView()
    }

    private fun initView() {
        ui.noteTitleEdit.setText(this.note?.title ?: "")
        ui.noteBodyEdit.setText(this.note?.body ?: "")

        ui.root.setBackgroundColor(resources.getColor(getColorResource(this.note?.color), null))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun triggerSaveNote() {
        if (ui.noteTitleEdit.text == null || ui.noteBodyEdit.text!!.length < 3) {
            return
        }

        Handler().postDelayed({
            note = note?.copy(
                title = ui.noteTitleEdit.text.toString(),
                body = ui.noteBodyEdit.text.toString(),
                lastChanged = Date()
            ) ?: createNewNote()

            if (note != null) {
                viewModel.saveChanges(note!!)
            }
        }, SAVE_DELAY)
    }

    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        ui.noteTitleEdit.text.toString(),
        ui.noteBodyEdit.text.toString()
    )

    override fun renderData(data: Note?) {
        this.note = data
        initView()
    }
}