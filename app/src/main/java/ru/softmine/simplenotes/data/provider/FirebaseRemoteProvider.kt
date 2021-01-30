package ru.softmine.simplenotes.data.provider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FirebaseRemoteProvider() : RemoteDataProvider {

    companion object {
        private val TAG = FirebaseRemoteProvider::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()
    private val notesReference = db.collection(NOTES_COLLECTION)

    override fun subscribeToAllNotes(): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            notesReference.addSnapshotListener { snapshot, error ->
                error?.let {
                    Log.e(TAG, "Error getting notes. Message: ${error.message}")
                    value = NoteResult.Error(it)
                } ?: snapshot?.let {
                    val notes = it.map { doc -> doc.toObject(Note::class.java) }
                    value = NoteResult.Success(notes)
                }
            }
        }

    override fun getNoteById(id: String): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            notesReference.document(id).get()
                .addOnSuccessListener { doc ->
                    value = NoteResult.Success(doc.toObject(Note::class.java))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error getting note. Message: ${exception.message}")
                    value = NoteResult.Error(exception)
                }
        }

    override fun saveNote(note: Note): LiveData<NoteResult> =
        MutableLiveData<NoteResult>().apply {
            notesReference.document(note.id)
                .set(note)
                .addOnSuccessListener {
                    Log.d(TAG, "Note $note is saved")
                    value = NoteResult.Success(note)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error saving note $note. Message: ${exception.message}")
                    value = NoteResult.Error(exception)
                }
        }

}