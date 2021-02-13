package ru.softmine.simplenotes.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import ru.softmine.simplenotes.data.model.User

@ExperimentalCoroutinesApi
class SplashViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private val mockRepository: Repository = mockk()
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        viewModel = SplashViewModel(mockRepository)
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `should call requestUser once`() {
        var result: User?
        val testData = User("test", "test@example.com")

        every { mockRepository.getCurrentUser() } returns testData

        runBlocking {
            result =  mockRepository.getCurrentUser()
        }
        verify(exactly = 1) { mockRepository.getCurrentUser() }
        assertEquals(result, testData)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}