package com.mynote.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mynote.app.navigation.Screen
import com.mynote.app.viewmodel.NoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: NoteViewModel,
    noteId: Long,
    onNavigateBack: () -> Unit
) {
    val isEditMode = noteId != Screen.Editor.NO_ID
    val existingNote = remember { if (isEditMode) viewModel.getNoteById(noteId) else null }

    var textContent by rememberSaveable {
        mutableStateOf(existingNote?.content ?: "")
    }

    var selectedColor by rememberSaveable {
        mutableStateOf(existingNote?.color ?: "#FFF176")
    }

    var selectedPriority by rememberSaveable {
        mutableStateOf(existingNote?.priority ?: "Rendah")
    }

    val noteColors = listOf(
        "#FFF176", "#FF8A80", "#B9F6CA", "#80D8FF", "#FFFF8D", "#CFD8DC"
    )

    val priorities = listOf("Rendah", "Sedang", "Tinggi")

    val currentBackgroundColor = Color(android.graphics.Color.parseColor(selectedColor))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditMode) "Edit Catatan" else "Catatan Baru")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.saveNote(
                                id = if (isEditMode) noteId else null,
                                content = textContent,
                                color = selectedColor,
                                priority = selectedPriority
                            )
                            onNavigateBack()
                        },
                        enabled = textContent.isNotBlank()
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = "Simpan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(currentBackgroundColor)
        ) {
            // Color Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                noteColors.forEach { colorHex ->
                    val color = Color(android.graphics.Color.parseColor(colorHex))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (selectedColor == colorHex) 3.dp else 1.dp,
                                color = if (selectedColor == colorHex) MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = colorHex }
                    )
                }
            }

            // Priority Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Prioritas:", style = MaterialTheme.typography.labelLarge)
                priorities.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority) }
                    )
                }
            }

            TextField(
                value = textContent,
                onValueChange = { textContent = it },
                modifier = Modifier.fillMaxSize(),
                placeholder = { Text("Tulis catatanmu di sini...") },
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}