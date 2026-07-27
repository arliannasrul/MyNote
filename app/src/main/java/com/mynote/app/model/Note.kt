package com.mynote.app.model

/**
 * Note adalah "cetakan" (blueprint) satu catatan.
 */
data class Note(
    val id: Long,
    val content: String,
    val color: String = "#FFF176",
    val priority: String = "Rendah",
    val isCompleted: Boolean = false, // Menandakan apakah catatan sudah selesai
    val updatedAt: Long = System.currentTimeMillis()
)