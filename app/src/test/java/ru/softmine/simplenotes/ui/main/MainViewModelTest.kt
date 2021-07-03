package ru.softmine.simplenotes.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.data.model.NoteResult

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val mockRepository: Repository = mockk()
    private val receiveChannel = Channel<NoteResult>()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        every { mockRepository.getNotes() } returns receiveChannel
        viewModel = MainViewModel(mockRepository)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `should call getNotes once`() {
        runBlocking {
            verify(exactly = 1) { mockRepository.getNotes() }
        }
    }

    @Test
    fun `should return error`() {
        var result: Throwable? = null
        val testData = Throwable("Error")

        runBlocking {
            viewModel.getViewState().consumeEach {
                result = (it as? NoteResult.Error)?.error
            }
        }
        assertEquals(result, testData)
    }

    @Test
    fun `should return Notes`() {
        var result: List<Note>? = null
        val testData = listOf(Note(id = "1"), Note(id = "2"))

        runBlocking {
            viewModel.getViewState().consumeEach {
                result = it
            }
        }

        assertEquals(testData, result)
    }

    @Test
    fun `should remove observer`() {
        viewModel.onCleared()
        assertTrue(receiveChannel.isClosedForReceive)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}