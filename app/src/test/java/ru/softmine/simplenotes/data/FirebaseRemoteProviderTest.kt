package ru.softmine.simplenotes.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.softmine.simplenotes.data.errors.NoAuthException
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult
import ru.softmine.simplenotes.data.provider.FirebaseRemoteProvider

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class FirebaseRemoteProviderTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockDb = mockk<FirebaseFirestore>()
    private val mockAuth = mockk<FirebaseAuth>()
    private val mockCollection = mockk<CollectionReference>()
    private val mockUser = mockk<FirebaseUser>()
    private val mockDocument1 = mockk<DocumentSnapshot>()
    private val mockDocument2 = mockk<DocumentSnapshot>()
    private val mockDocument3 = mockk<DocumentSnapshot>()
    private val testNotes = listOf(Note(id = "1"), Note(id = "2"), Note(id = "3"))

    private val provider: FirebaseRemoteProvider = FirebaseRemoteProvider(mockAuth, mockDb)

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        every { mockAuth.currentUser } returns mockUser
        every { mockUser.uid } returns ""
        every {
            mockDb.collection(any()).document(any()).collection(any())
        } returns mockCollection
        every { mockDocument1.toObject(Note::class.java) } returns testNotes[0]
        every { mockDocument2.toObject(Note::class.java) } returns testNotes[1]
        every { mockDocument3.toObject(Note::class.java) } returns testNotes[2]
    }

    @Test
    fun `should throw if no auth`() {
        var result: Any? = null
        every { mockAuth.currentUser } returns null

        runBlocking {
            launch(Dispatchers.Main) {
                provider.subscribeToAllNotes().consumeEach {
                    result = (it as? NoteResult.Error)?.error
                }
            }
        }
        assertTrue(result is NoAuthException)
    }

    @Test
    fun `subscribeToAllNotes return notes`() {
        var result: List<Note>? = null
        val slot = slot<EventListener<QuerySnapshot>>()
        val mockSnapshot = mockk<QuerySnapshot>()

        every { mockSnapshot.documents } returns
                listOf(mockDocument1, mockDocument2, mockDocument3)
        every { mockCollection.addSnapshotListener(capture(slot)) } returns mockk()

        runBlocking {
            launch(Dispatchers.Main) {
                provider.subscribeToAllNotes().consumeEach {
                    result = (it as? NoteResult.Success<List<Note>>)?.data
                }
            }
        }

        slot.captured.onEvent(mockSnapshot, null)

        assertEquals(testNotes, result)
    }

    @Test
    fun `subscribeAllNotes return error`() {
        var result: Throwable? = null
        val slot = slot<EventListener<QuerySnapshot>>()
        val testError = mockk<FirebaseFirestoreException>(relaxed = true)

        every { mockCollection.addSnapshotListener(capture(slot)) } returns mockk()

        runBlocking {
            launch(Dispatchers.Main) {
                provider.subscribeToAllNotes().consumeEach {
                    result = (it as? NoteResult.Error)?.error
                }
            }
        }

        slot.captured.onEvent(null, testError)

        assertNotNull(result)
        assertEquals(testError, result)
    }

    @Test
    fun `getNoteById return Note`() {
        var result: Note? = null
        val mockDocumentReference: DocumentReference = mockk()
        val mockDocumentSnapshot: DocumentSnapshot = mockk()
        val slot = slot<OnSuccessListener<in DocumentSnapshot>>()

        every { mockCollection.document(any()) } returns mockDocumentReference
        every {
            mockDocumentReference.get().addOnSuccessListener(capture(slot))
        } returns mockk()
        every { mockDocumentSnapshot.toObject(Note::class.java) } returns testNotes[0]

        runBlocking {
            launch(Dispatchers.Main) {
                result = provider.getNoteById(testNotes[0].id)
            }
        }

        slot.captured.onSuccess(mockDocumentSnapshot)

        assertEquals(testNotes[0], result)
    }

    @Test
    fun `getNoteById return error`() {
        val result: Throwable? = null
        val testError = mockk<FirebaseFirestoreException>()
        val mockDocumentReference: DocumentReference = mockk()

        every { mockCollection.document(any()) } returns mockDocumentReference
        every { mockDocumentReference.get() } throws testError

        runBlocking {
            launch(Dispatchers.Main) {
                provider.getNoteById(testNotes[0].id)
            }
        }

        assertNotNull(result)
        assertEquals(testError, result)
    }

    @Test
    fun `saveNote calls document set`() {
        val mockDocumentReference: DocumentReference = mockk()
        every { mockCollection.document(testNotes[0].id) } returns
                mockDocumentReference

        runBlocking {
            launch(Dispatchers.Main) {
                provider.saveNote(testNotes[0])
            }
        }

        verify(exactly = 1) { mockDocumentReference.set(testNotes[0]) }
    }

    @Test
    fun `saveNote return Note`() {
        val mockDocumentReference: DocumentReference = mockk()
        val slot = slot<OnSuccessListener<in Void>>()
        var result: Note? = null

        every { mockCollection.document(testNotes[0].id) } returns mockDocumentReference
        every {
            mockDocumentReference.set(testNotes[0]).addOnSuccessListener(capture(slot))
        } returns mockk()

        runBlocking {
            launch(Dispatchers.Main) {
                result = provider.saveNote(testNotes[0])
            }
        }
        slot.captured.onSuccess(null)

        assertNotNull(result)
        assertEquals(testNotes[0], result)
    }

    @Test
    fun `saveNote return error`() {
        val mockDocumentReference: DocumentReference = mockk()
        val testError = mockk<FirebaseFirestoreException>()
        val result: Throwable? = null

        every { mockCollection.document(testNotes[0].id) } returns mockDocumentReference
        every { mockDocumentReference.set(testNotes[0]) } throws testError

        runBlocking {
            launch(Dispatchers.Main) {
                provider.saveNote(testNotes[0])
            }
        }

        assertNotNull(result)
        assertEquals(testError, result)
    }

    @After
    fun tearDown() {
        clearMocks(mockCollection, mockDocument1, mockDocument2, mockDocument3)
        Dispatchers.resetMain()
    }
}