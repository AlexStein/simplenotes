package ru.softmine.simplenotes.ui.note

import androidx.lifecycle.ViewModel
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note

class NoteViewModel(private val repository: Repository = Repository): ViewModel() {

    private var pendingNote : Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            repository.saveNote(pendingNote!!)
        }
    }
}