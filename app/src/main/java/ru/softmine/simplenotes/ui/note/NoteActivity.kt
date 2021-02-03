package ru.softmine.simplenotes.ui.note

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import org.koin.android.viewmodel.ext.android.viewModel
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.common.Color
import ru.softmine.simplenotes.common.format
import ru.softmine.simplenotes.common.getColorInt
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ActivityNoteBinding
import ru.softmine.simplenotes.ui.base.BaseActivity
import java.util.*

private const val SAVE_DELAY = 2000L

class NoteActivity : BaseActivity<NoteViewState.Data, NoteViewState>() {

    companion object {
        private val EXTRA_NOTE = NoteActivity::class.java.name + "extra.NOTE"

        fun getNoteStartIntent(context: Context, noteId: String?): Intent {
            val intent = Intent(context, NoteActivity::class.java)
            intent.putExtra(EXTRA_NOTE, noteId)
            return intent
        }
    }

    override val model: NoteViewModel by viewModel()
    override val layoutRes = R.layout.activity_note
    override val ui: ActivityNoteBinding by lazy {
        ActivityNoteBinding.inflate(layoutInflater)
    }
    private var note: Note? = null
    private var color: Color = Color.BLUE

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

        noteId?.let {
            model.loadNote(it)
        } ?: run {
            supportActionBar?.title = getString(R.string.new_note_title)
        }

        ui.colorPicker.onColorClickListener = { c ->
            color = c
            setBackgroundColor(c)
            triggerSaveNote()
        }

        setEditListeners()
    }

    private fun initView() {
        note?.run {
            removeEditListeners()
            if (ui.noteTitleEdit.text.toString() != title) {
                ui.noteTitleEdit.setText(title)
            }
            if (ui.noteBodyEdit.text.toString() != body) {
                ui.noteBodyEdit.setText(body)
            }
            setEditListeners()
            supportActionBar?.title = lastChanged.format()
            setBackgroundColor(color)
        }
    }

    private fun setEditListeners() {
        ui.noteTitleEdit.addTextChangedListener(textChangeListener)
        ui.noteBodyEdit.addTextChangedListener(textChangeListener)
    }

    private fun removeEditListeners() {
        ui.noteTitleEdit.removeTextChangedListener(textChangeListener)
        ui.noteBodyEdit.removeTextChangedListener(textChangeListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean =
        MenuInflater(this).inflate(R.menu.menu_note, menu).let { true }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> super.onBackPressed().let { true }
        R.id.palette -> togglePalette().let { true }
        R.id.delete -> deleteNote().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    private fun deleteNote() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_title)
            .setMessage(R.string.delete_message)
            .setPositiveButton(R.string.ok_button) { _, _ -> model.deleteNote() }
            .setNegativeButton(R.string.cancel_button) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun triggerSaveNote() {
        ui.noteTitleEdit.text?.let {
            if (it.length < 3) return

            Handler().postDelayed({
                note = note?.copy(
                    title = ui.noteTitleEdit.text.toString(),
                    body = ui.noteBodyEdit.text.toString(),
                    lastChanged = Date(),
                    color = color
                ) ?: createNewNote()

                note?.let { n -> model.saveChanges(n) }
            }, SAVE_DELAY)
        }
    }

    private fun createNewNote(): Note = Note(
        UUID.randomUUID().toString(),
        ui.noteTitleEdit.text.toString(),
        ui.noteBodyEdit.text.toString()
    )

    override fun renderData(data: NoteViewState.Data) {
        if (data.isDeleted) finish()

        this.note = data.note
        data.note?.let { color = it.color }
        initView()
    }

    private fun setBackgroundColor(color: Color) {
        ui.root.setBackgroundColor(color.getColorInt(this@NoteActivity))
    }

    private fun togglePalette() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
        } else {
            ui.colorPicker.open()
        }
    }

    override fun onBackPressed() {
        if (ui.colorPicker.isOpen) {
            ui.colorPicker.close()
            return
        }
        super.onBackPressed()
    }
}
