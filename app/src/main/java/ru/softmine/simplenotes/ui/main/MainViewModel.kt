package ru.softmine.simplenotes.ui.main

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import ru.softmine.simplenotes.ui.base.BaseViewModel

@ExperimentalCoroutinesApi
class MainViewModel(repository: Repository) :
    BaseViewModel<List<Note>?>() {

    private val notesChannel = repository.getNotes()

    init {
        launch {
            notesChannel.consumeEach {
                when (it) {
                    is NoteResult.Success<*> -> setData(it.data as? List<Note>)
                    is NoteResult.Error -> setError(it.error)
                }
            }
        }
    }

    @VisibleForTesting
    public override fun onCleared() {
        notesChannel.cancel()
        super.onCleared()
    }
}