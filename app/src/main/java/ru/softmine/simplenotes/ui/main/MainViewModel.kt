package ru.softmine.simplenotes.ui.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import ru.softmine.simplenotes.ui.base.BaseViewModel

class MainViewModel(repository: Repository) :
    BaseViewModel<List<Note>?, MainViewState>() {
    private val repositoryNotes = repository.getNotes()

    private val notesObserver =
        Observer<NoteResult> { t ->
            t?.let {
                when (it) {
                    is NoteResult.Success<*> -> {
                        viewStateLiveData.value = MainViewState(notes = it.data as? List<Note>)
                    }
                    is NoteResult.Error -> {
                        viewStateLiveData.value = MainViewState(error = it.error)
                    }
                }
            }
        }

    init {
        viewStateLiveData.value = MainViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    fun viewState(): LiveData<MainViewState> = viewStateLiveData

    @VisibleForTesting
    public override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }
}