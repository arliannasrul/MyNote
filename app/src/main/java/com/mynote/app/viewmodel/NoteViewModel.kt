package com.mynote.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mynote.app.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("mynote_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _notes = MutableStateFlow<List<Note>>(loadNotes())
    val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    private var nextId = if (_notes.value.isEmpty()) 1L else (_notes.value.maxOf { it.id } + 1)

    private fun loadNotes(): List<Note> {
        val json = sharedPrefs.getString("notes_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Note>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveToPrefs(notes: List<Note>) {
        val json = gson.toJson(notes)
        sharedPrefs.edit().putString("notes_list", json).apply()
    }

    fun getNoteById(id: Long): Note? = _notes.value.find { it.id == id }

    fun saveNote(id: Long?, content: String, color: String, priority: String) {
        if (content.isBlank()) return

        _notes.update { currentList ->
            val updatedList = if (id == null) {
                listOf(Note(id = nextId++, content = content.trim(), color = color, priority = priority)) + currentList
            } else {
                currentList.map { note ->
                    if (note.id == id) {
                        note.copy(
                            content = content.trim(),
                            color = color,
                            priority = priority,
                            updatedAt = System.currentTimeMillis()
                        )
                    } else {
                        note
                    }
                }
            }
            saveToPrefs(updatedList)
            updatedList
        }
    }

    fun toggleNoteCompletion(id: Long) {
        _notes.update { currentList ->
            val updatedList = currentList.map { note ->
                if (note.id == id) {
                    note.copy(isCompleted = !note.isCompleted)
                } else {
                    note
                }
            }
            saveToPrefs(updatedList)
            updatedList
        }
    }

    fun deleteNote(id: Long) {
        _notes.update { currentList ->
            val updatedList = currentList.filterNot { it.id == id }
            saveToPrefs(updatedList)
            updatedList
        }
    }
}