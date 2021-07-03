package ru.softmine.simplenotes.ui.note

import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.ui.base.BaseViewState

class NoteViewState(data: Data = Data(), error: Throwable? = null) :
    BaseViewState<NoteViewState.Data>(data, error) {
    data class Data(val isDeleted: Boolean = false, val note: Note? = null)
}