package ru.softmine.simplenotes.ui.note

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.Note

class NoteViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository: Repository = mockk()
    private val noteLiveData = MutableLiveData<NoteViewState>()
    private lateinit var viewModel: NoteViewModel

    @Before
    fun setUp() {
        viewModel = NoteViewModel(mockRepository)
    }

    @Test
    fun `loadNote should return Note`() {
        var result: NoteViewState.Data? = null
        val testNote = Note("1", "title1", "body1")
        val testData = NoteViewState.Data(note = testNote)

        viewModel.getViewState().observeForever { result = it.data }
        viewModel.viewStateLiveData.value = NoteViewState(data = testData)

        assertEquals(testData.note, result?.note)
    }

    @Test
    fun `deleteNote should set isDeleted flag`() {
        var result: NoteViewState.Data? = null
        val testData = NoteViewState.Data(isDeleted = true)

        viewModel.getViewState().observeForever { result = it.data }
        viewModel.viewStateLiveData.value = NoteViewState(data = testData)

        assertEquals(testData.isDeleted, result?.isDeleted)
    }

}