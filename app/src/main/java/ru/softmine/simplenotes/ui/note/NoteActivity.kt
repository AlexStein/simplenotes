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
import ru.softmine.simplenotes.common.format
import ru.softmine.simplenotes.common.getColorInt
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
    override val ui: ActivityNoteBinding by lazy {
        ActivityNoteBinding.inflate(layoutInflater)
    }
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
        setContentView(ui.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteId?.let { viewModel.loadNote(it) }
        supportActionBar?.title = getString(R.string.new_note_title)

        initView()

        ui.noteTitleEdit.addTextChangedListener(textChangeListener)
        ui.noteBodyEdit.addTextChangedListener(textChangeListener)
    }

    private fun initView() {
        note?.run {
            ui.noteTitleEdit.setText(title)
            ui.noteBodyEdit.setText(body)
            supportActionBar?.title = lastChanged.format()
            ui.root.setBackgroundColor(color.getColorInt(this@NoteActivity))
        }
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
        ui.noteTitleEdit.text?.let {
            if (it.length < 3) return

            Handler().postDelayed({
                note = note?.copy(
                    title = ui.noteTitleEdit.text.toString(),
                    body = ui.noteBodyEdit.text.toString(),
                    lastChanged = Date()
                ) ?: createNewNote()

                note?.let { viewModel.saveChanges(it) }
            }, SAVE_DELAY)
        }
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
