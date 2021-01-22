package ru.softmine.simplenotes.data

import ru.softmine.simplenotes.data.model.Note

object Repository {
    val notes: List<Note> = listOf(
        Note("Первая заметка", "Это первая заметка", 0xffffdddd.toInt()),
        Note("Вторая заметка", "Это вторая заметка", 0xffddffdd.toInt()),
        Note("Третья заметка", "Это третья заметка", 0xffddddff.toInt()))

}