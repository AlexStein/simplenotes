package ru.softmine.simplenotes.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.softmine.simplenotes.common.Color
import ru.softmine.simplenotes.data.model.Note
import java.util.*

object Repository {

    private val notesLiveData = MutableLiveData<List<Note>>()

    private val notes: MutableList<Note> = mutableListOf(
        Note(
            id = UUID.randomUUID().toString(),
            title = "Первая заметка",
            body = "Это первая заметка",
            color = Color.WHITE
        ),
        Note(
            id = UUID.randomUUID().toString(),
            title = "Вторая заметка",
            body = "Это вторая заметка",
            color = Color.RED
        ),
        Note(
            id = UUID.randomUUID().toString(),
            title = "Третья заметка",
            body = "Это третья заметка",
            color = Color.GREEN
        )
    )

    init {
        notesLiveData.value = notes
    }

    fun getNotes() : LiveData<List<Note>> {
        return notesLiveData
    }

    fun saveNote(note: Note) {
        addOrUpdate(note)
        notesLiveData.value = notes
    }

    private fun addOrUpdate(note: Note) {

        for (i in 0 until notes.size) {
            if (notes[i] == note) {
                notes[i] = note
                return
            }
        }

        notes.add(note)
    }

}