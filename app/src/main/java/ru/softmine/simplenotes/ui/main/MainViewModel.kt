package ru.softmine.simplenotes.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.softmine.simplenotes.data.Repository

class MainViewModel(private val repository: Repository = Repository) : ViewModel() {

    private val viewStateLiveData: MutableLiveData<MainViewState> = MutableLiveData()

    init {
        repository.getNotes().observeForever { it ->
            viewStateLiveData.value =
                viewStateLiveData.value?.copy(notes = it!!) ?: MainViewState(it)
        }
    }

    fun viewState(): LiveData<MainViewState> = viewStateLiveData
}