package ru.softmine.simplenotes.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.softmine.simplenotes.data.Repository
import ru.softmine.simplenotes.data.model.User

class SplashViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val mockRepository: Repository = mockk()
    private val userLiveData = MutableLiveData<User?>()
    private lateinit var viewModel: SplashViewModel

    @Before
    fun setUp() {
        viewModel = SplashViewModel(mockRepository)
    }

    @Test
    fun `should call requestUser once`() {
        var result: User? = null
        val testData = User("test", "test@example.com")

        every { mockRepository.getCurrentUser() } returns userLiveData

        mockRepository.getCurrentUser().observeForever { result = it }
        userLiveData.value = testData

        verify(exactly = 1) { mockRepository.getCurrentUser() }
        assertEquals(result, testData)
    }
}