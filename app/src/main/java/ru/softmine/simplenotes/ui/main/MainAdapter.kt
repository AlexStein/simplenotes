package ru.softmine.simplenotes.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.softmine.simplenotes.R
import ru.softmine.simplenotes.common.getColorResource
import ru.softmine.simplenotes.data.model.Note
import ru.softmine.simplenotes.databinding.ItemNoteBinding

interface OnItemClickListener {
    fun onItemClick(note: Note)
}

class MainAdapter(private val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {

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

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ui: ItemNoteBinding by lazy { ItemNoteBinding.bind(itemView) }

        fun bind(note: Note) {
            with(note) {
                ui.noteTitle.text = title
                ui.noteBody.text = body
                itemView.setBackgroundColor(
                    itemView.context.resources.getColor(color.getColorResource(), null)
                )
                itemView.setOnClickListener { onItemClickListener.onItemClick(note) }
            }
        }
    }
}