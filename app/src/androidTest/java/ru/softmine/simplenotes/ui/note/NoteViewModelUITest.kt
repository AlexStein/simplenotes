package ru.softmine.simplenotes.ui.note

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import io.mockk.*
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.common.Color
import ru.softmine.simplenotes.common.getColorInt
import ru.softmine.simplenotes.data.model.Note

private const val SAVE_DELAY = 2000L

class NoteViewModelUITest {

    @get:Rule
    val activityTestRule = ActivityTestRule(NoteActivity::class.java, true, false)

    private val viewModel: NoteViewModel = spyk(NoteViewModel(mockk()))
    private val viewStateLiveData = MutableLiveData<NoteViewState>()
    private val testNote = Note("1", "title", "body")

    @Before
    fun setUp() {
        startKoin { modules() }
        loadKoinModules(
            module {
                viewModel { viewModel }
            }
        )
        every { viewModel.getViewState() } returns viewStateLiveData
        every { viewModel.loadNote(any()) } just runs
        every { viewModel.saveChanges(any()) } just runs
        every { viewModel.deleteNote() } just runs

        Intent().apply {
            putExtra(NoteActivity::class.java.name + "extra.NOTE", testNote.id)
        }.let {
            activityTestRule.launchActivity(it)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun should_show_color_picker() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.change_color_menu_title)).perform(click())

        onView(withId(R.id.colorPicker)).check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun should_hide_color_picker() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.change_color_menu_title)).perform(click())

        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.change_color_menu_title)).perform(click())

        onView(withId(R.id.colorPicker)).check(matches(not(isDisplayed())))
    }

    @Test
    fun should_set_background_color() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.change_color_menu_title)).perform(click())
        onView(withTagValue(`is`(Color.BLUE))).perform(click())

        val colorInt = Color.BLUE.getColorInt(activityTestRule.activity)

        onView(withId(R.id.layout)).check { view, _ ->
            assertTrue(
                "toolbar background color does not match",
                (view.background as? ColorDrawable)?.color == colorInt
            )
        }
    }

    @Test
    fun should_call_viewModel_loadNote() {
        verify(exactly = 1) { viewModel.loadNote(testNote.id) }
    }

    @Test
    fun should_show_note() {
        activityTestRule.launchActivity(null)
        viewStateLiveData.postValue(NoteViewState(NoteViewState.Data(note = testNote)))

        onView(withId(R.id.noteTitleEdit)).check(matches(withText(testNote.title)))
        onView(withId(R.id.noteBodyEdit)).check(matches(withText(testNote.body)))
    }

    @Test
    fun should_call_saveNote() {
        onView(withId(R.id.noteTitleEdit)).perform(typeText(testNote.title))
        verify(timeout = SAVE_DELAY) { viewModel.saveChanges(any()) }
    }

    @Test
    fun should_call_deleteNote() {
        openActionBarOverflowOrOptionsMenu(activityTestRule.activity)
        onView(withText(R.string.delete_menu_title)).perform(click())
        onView(withText(R.string.ok_button)).perform(click())
        verify { viewModel.deleteNote() }
    }
}