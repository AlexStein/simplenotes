package ru.softmine.simplenotes.ui.note

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note

@ExperimentalCoroutinesApi
class NoteViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val mockRepository: Repository = mockk()
    private val receiveChannel = Channel<NoteViewState.Data>()
    private lateinit var viewModel: NoteViewModel

    @Before
    fun setUp() {
        viewModel = NoteViewModel(mockRepository)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `loadNote should return Note`() {
        var result: NoteViewState.Data?
        val testNote = Note("1", "title1", "body1")
        val testData = NoteViewState.Data(note = testNote)

        runBlocking {
            viewModel.getViewState().let { result = it.receive() }
        }
        assertEquals(testData.note, result?.note)
    }

    @Test
    fun `deleteNote should set isDeleted flag`() {
        var result: NoteViewState.Data?
        val testData = NoteViewState.Data(isDeleted = true)

        runBlocking {
            viewModel.getViewState().let { result = it.receive() }
        }

        assertEquals(testData.isDeleted, result?.isDeleted)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}