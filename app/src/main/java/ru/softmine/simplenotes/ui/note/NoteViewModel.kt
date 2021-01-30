package ru.softmine.simplenotes.ui.note

import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import ru.softmine.simplenotes.ui.base.BaseViewModel

class NoteViewModel(private val repository: Repository = Repository) :
    BaseViewModel<Note?, NoteViewState>() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever { t ->
            t?.let {
                when (it) {
                    is NoteResult.Success<*> -> {
                        viewStateLiveData.value = NoteViewState(note = it.data as? Note)
                    }
                    is NoteResult.Error -> {
                        viewStateLiveData.value = NoteViewState(error = it.error)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        pendingNote?.let { repository.saveNote(it) }
    }
}