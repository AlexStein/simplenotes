package ru.softmine.simplenotes.data.provider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.core.QueryListener
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import java.lang.Exception

private const val NOTES_COLLECTION = "notes"

class FirebaseRemoteProvider() : RemoteDataProvider {

    companion object {
        private val TAG = FirebaseRemoteProvider::class.java.simpleName
    }

    private val db = FirebaseFirestore.getInstance()
    private val notesReference = db.collection(NOTES_COLLECTION)

    override fun subscribeToAllNotes(): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesReference.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(snapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Log.e(TAG, "Error getting notes. Message: ${error.message}")
                    result.value = NoteResult.Error(error)
                } else if (snapshot != null) {
                    val notes = mutableListOf<Note>()
                    for (doc: QueryDocumentSnapshot in snapshot) {
                        notes.add(doc.toObject(Note::class.java))
                    }
                    result.value = NoteResult.Success(notes)
                }
            }
        })

        return result
    }

    override fun getNoteById(id: String): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesReference.document(id).get()
            .addOnSuccessListener(object : OnSuccessListener<DocumentSnapshot> {
                override fun onSuccess(doc: DocumentSnapshot) {
                    result.value = NoteResult.Success(doc.toObject(Note::class.java))
                }

            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error getting note. Message: ${exception.message}")
                    result.value = NoteResult.Error(exception)
                }
            })

        return result
    }

    override fun saveNote(note: Note): LiveData<NoteResult> {
        val result = MutableLiveData<NoteResult>()

        notesReference.document(note.id)
            .set(note)
            .addOnSuccessListener(object : OnSuccessListener<Void> {
                override fun onSuccess(p0: Void?) {
                    Log.d(TAG, "Note $note is saved")
                    result.value = NoteResult.Success(note)
                }
            })
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(exception: Exception) {
                    Log.e(TAG, "Error saving note $note. Message: ${exception.message}")
                    result.value = NoteResult.Error(exception)
                }
            })

        return result
    }
}