package ru.softmine.simplenotes.ui.note

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.common.DATETIME_FORMAT
import ru.softmine.simplenotes.common.getColorResource
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ActivityNoteBinding
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : AppCompatActivity() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"

        fun getNoteStartIntent(context: Context, note: Note?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, note)
            return intent
        }
    }

    private lateinit var ui: ActivityNoteBinding
    private var note: Note? = null

    private lateinit var viewModel: NoteViewModel

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
        ui = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(ui.root)

        note = intent.getParcelableExtra(EXTRA_NOTE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title = if (note != null) {
            SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).format(note!!.lastChanged)
        } else {
            getString(R.string.new_note_title)
        }

        viewModel = ViewModelProviders.of(this).get(NoteViewModel::class.java)

        initView()
    }

    private fun initView() {
        ui.noteTitleEdit.setText(note?.title ?: "noteTitleEdit")
        ui.noteTitleEdit.addTextChangedListener(textChangeListener)

        ui.noteBodyEdit.setText(note?.body ?: "noteBodyEdit")
        ui.noteBodyEdit.addTextChangedListener(textChangeListener)

        ui.root.setBackgroundColor(resources.getColor(getColorResource(note?.color), null))
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
}