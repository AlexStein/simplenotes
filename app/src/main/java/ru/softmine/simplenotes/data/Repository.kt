package ru.softmine.simplenotes.data

import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.provider.RemoteDataProvider

class Repository(private val remoteProvider: RemoteDataProvider) {

    fun getNotes() = remoteProvider.subscribeToAllNotes()
    suspend fun saveNote(note: Note) = remoteProvider.saveNote(note)
    suspend fun deleteNote(noteId: String) = remoteProvider.deleteNote(noteId)
    suspend fun getNoteById(id: String) = remoteProvider.getNoteById(id)
    fun getCurrentUser() = remoteProvider.getCurrentUser()
}