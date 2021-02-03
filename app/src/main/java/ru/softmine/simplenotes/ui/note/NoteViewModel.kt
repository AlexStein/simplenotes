package ru.softmine.simplenotes.ui.note

import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import ru.softmine.simplenotes.ui.base.BaseViewModel

class NoteViewModel(private val repository: Repository) :
    BaseViewModel<NoteViewState.Data, NoteViewState>() {

    private val currentNote: Note?
        get() = viewStateLiveData.value?.data?.note

    fun saveChanges(note: Note) {
        viewStateLiveData.value = NoteViewState(NoteViewState.Data(note = note))
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever { t ->
            t?.let {
                when (it) {
                    is NoteResult.Success<*> -> {
                        viewStateLiveData.value =
                            NoteViewState(NoteViewState.Data(note = it.data as? Note))
                    }
                    is NoteResult.Error -> {
                        viewStateLiveData.value = NoteViewState(error = it.error)
                    }
                }
            }
        }
    }

    fun deleteNote() {
        currentNote?.let { note ->
            repository.deleteNote(note.id).observeForever { t ->
                t?.let {
                    viewStateLiveData.value = when (it) {
                        is NoteResult.Success<*> -> NoteViewState(NoteViewState.Data(isDeleted = true))
                        is NoteResult.Error -> NoteViewState(error = it.error)
                    }
                }
            }
        }
    }

    override fun onCleared() {
        currentNote?.let { repository.saveNote(it) }
    }
}