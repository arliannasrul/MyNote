package com.mynote.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mynote.app.model.Note
import com.mynote.app.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: NoteViewModel,
    onAddNote: () -> Unit,
    onNoteClick: (Long) -> Unit,
    onAboutClick: () -> Unit
) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MyNote 📝", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onAboutClick) {
                        Icon(Icons.Filled.Info, contentDescription = "Tentang Aplikasi")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddNote,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Catatan") }
            )
        }
    ) { innerPadding ->
        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada catatan.\nTekan \"+ Catatan\" untuk memulai ✍️",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = notes,
                    key = { note -> note.id }
                ) { note ->
                    NoteCard(
                        note = note, 
                        onClick = { onNoteClick(note.id) },
                        onCheckedChange = { viewModel.toggleNoteCompletion(note.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (note.isCompleted) 0.6f else 1f),
        colors = CardDefaults.cardColors(
            containerColor = Color(android.graphics.Color.parseColor(note.color))
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = note.isCompleted,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = 8.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodyLarge,
                        textDecoration = if (note.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    PriorityBadge(note.priority)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Diperbarui: ${formatTanggal(note.updatedAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val backgroundColor = when (priority) {
        "Tinggi" -> Color(0xFFFFCDD2) // Red
        "Sedang" -> Color(0xFFFFF9C4) // Yellow
        else -> Color(0xFFC8E6C9)     // Green
    }
    
    val textColor = when (priority) {
        "Tinggi" -> Color(0xFFC62828)
        "Sedang" -> Color(0xFFF9A825)
        else -> Color(0xFF2E7D32)
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = priority,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = textColor
        )
    }
}

private fun formatTanggal(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    return formatter.format(Date(millis))
}