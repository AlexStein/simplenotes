package ru.softmine.simplenotes.data

import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.provider.FirebaseRemoteProvider
import ru.softmine.simplenotes.data.provider.RemoteDataProvider

object Repository {

    private val remoteProvider: RemoteDataProvider = FirebaseRemoteProvider()

    fun getNotes() = remoteProvider.subscribeToAllNotes()
    fun saveNote(note: Note) = remoteProvider.saveNote(note)
    fun getNoteById(id: String) = remoteProvider.getNoteById(id)
    fun getCurrentUser() = remoteProvider.getCurrentUser()
}