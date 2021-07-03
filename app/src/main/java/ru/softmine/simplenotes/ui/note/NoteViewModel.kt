package ru.softmine.simplenotes.ui.note

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.ui.base.BaseViewModel

@ExperimentalCoroutinesApi
class NoteViewModel(private val repository: Repository) :
    BaseViewModel<NoteViewState.Data>() {

    private val currentNote: Note?
        get() = getViewState().poll()?.note

    fun saveChanges(note: Note) {
        setData(NoteViewState.Data(note = note))
    }

    fun loadNote(noteId: String) {
        launch {
            try {
                setData(NoteViewState.Data(note = repository.getNoteById(noteId)))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    fun deleteNote() {
        launch {
            try {
                currentNote?.let { repository.deleteNote(it.id) }
                setData(NoteViewState.Data(isDeleted = true))
            } catch (e: Throwable) {
                setError(e)
            }
        }
    }

    override fun onCleared() {
        launch {
            currentNote?.let { repository.saveNote(it) }
            super.onCleared()
        }
    }
}