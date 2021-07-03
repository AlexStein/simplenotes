package ru.softmine.simplenotes.data.provider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import ru.softmine.simplenotes.data.errors.NoAuthException
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import ru.softmine.simplenotes.data.model.User
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FirebaseRemoteProvider(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : RemoteDataProvider {

    private val currentUser
        get() = firebaseAuth.currentUser

    @ExperimentalCoroutinesApi
    override fun subscribeToAllNotes(): ReceiveChannel<NoteResult> =
        Channel<NoteResult>(Channel.CONFLATED).apply {
            var registration: ListenerRegistration? = null

            try {
                registration =
                    getUserNotesCollection().addSnapshotListener { snapshot,
                                                                   e ->
                        val value = e?.let {
                            NoteResult.Error(it)
                        } ?: snapshot?.let {
                            val notes = it.documents.map { doc ->
                                doc.toObject(Note::class.java)
                            }
                            NoteResult.Success(notes)
                        }

                        value?.let { offer(it) }
                    }
            } catch (e: Throwable) {
                offer(NoteResult.Error(e))
            }

            invokeOnClose { registration?.remove() }
        }

    override suspend fun getNoteById(id: String): Note =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(id).get()
                    .addOnSuccessListener {
                        continuation.resume(it.toObject(Note::class.java)!!)
                    }.addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }

    override suspend fun saveNote(note: Note): Note =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(note.id).set(note)
                    .addOnSuccessListener {
                        continuation.resume(note)
                    }.addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }

    override suspend fun deleteNote(noteId: String): Note? =
        suspendCoroutine { continuation ->
            try {
                getUserNotesCollection().document(noteId).delete()
                    .addOnSuccessListener {
                        continuation.resume(null)
                    }.addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            } catch (e: Throwable) {
                continuation.resumeWithException(e)
            }
        }

    override fun getCurrentUser(): User? =
        currentUser?.let { User(it.displayName ?: "", it.email ?: "") }

    private fun getUserNotesCollection() = currentUser?.let {
        db.collection(USERS_COLLECTION).document(it.uid).collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()
}