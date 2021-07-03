package ru.softmine.simplenotes.ui.main

import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.ui.base.BaseViewState

class MainViewState(notes: List<Note>? = null, error: Throwable? = null) :
    BaseViewState<List<Note>?>(notes, error)