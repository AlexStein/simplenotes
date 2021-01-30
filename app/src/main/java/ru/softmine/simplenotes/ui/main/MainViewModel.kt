package ru.softmine.simplenotes.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import ru.softmine.simplenotes.ui.base.BaseViewModel

class MainViewModel(private val repository: Repository = Repository) :
    BaseViewModel<List<Note>?, MainViewState>() {

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

    private val repositoryNotes = repository.getNotes()

    fun viewState(): LiveData<MainViewState> = viewStateLiveData

    init {
        viewStateLiveData.value = MainViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }
}