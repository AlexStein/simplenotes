package ru.softmine.simplenotes.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ItemNoteBinding

class MainAdapter : RecyclerView.Adapter<NoteViewHolder>() {

    var notes: List<Note> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_note, parent, false)

        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount() = notes.size
}

class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val ui = ItemNoteBinding.bind(itemView)

    fun bind(note: Note) {
        with(note) {
            ui.noteTitle.text = title
            ui.noteBody.text = body
            itemView.setBackgroundColor(color)
        }
    }
}