package ru.softmine.simplenotes.ui.note

import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.ui.base.BaseViewState

class NoteViewState(note: Note? = null, error: Throwable? = null) :
    BaseViewState<Note?>(note, error)