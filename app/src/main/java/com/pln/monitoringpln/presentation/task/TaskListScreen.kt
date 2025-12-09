package com.pln.monitoringpln.presentation.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pln.monitoringpln.domain.model.Tugas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    state: TaskListState,
    onSearchQueryChange: (String) -> Unit,
    onAddTask: () -> Unit,
    onTaskClick: (String) -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daftar Tugas",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            if (state.isAdmin) {
                FloatingActionButton(
                    onClick = onAddTask,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari tugas, lokasi, atau teknisi...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )

            // Total Tasks Label
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Text(
                        text = "Total Tugas: ${state.filteredTasks.size}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }

            // Task List
            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp), // Space for FAB and BottomNav
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                items(state.filteredTasks) { task ->
                    TaskItem(
                        task = task,
                        technicianNames = state.technicianNames,
                        equipmentNames = state.equipmentNames,
                        onClick = { onTaskClick(task.id) },
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Tugas,
    technicianNames: Map<String, String>,
    equipmentNames: Map<String, String>,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.judul,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val technicianName = technicianNames[task.idTeknisi] ?: task.idTeknisi
                    Text(
                        text = "Teknisi: $technicianName",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                // Status Badge
                val status = task.status.replace("_", " ")
                Surface(
                    color = when {
                        status.equals("Done", ignoreCase = true) -> Color(0xFFE6F4EA)
                        status.equals("In Progress", ignoreCase = true) -> Color(0xFFFEF7E0)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = status.lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString() }
                            .split(" ").joinToString(" ") {
                                it.replaceFirstChar { char ->
                                    if (char.isLowerCase()) char.titlecase(java.util.Locale.ROOT) else char.toString()
                                }
                            },
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            status.equals("Done", ignoreCase = true) -> Color(0xFF137333)
                            status.equals("In Progress", ignoreCase = true) -> Color(0xFFB06000)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                val equipmentName = equipmentNames[task.idAlat] ?: task.idAlat
                Text(
                    text = "Alat: $equipmentName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.weight(1f))

                // Format Date
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                val dateString = try {
                    dateFormat.format(task.tglJatuhTempo)
                } catch (e: Exception) {
                    task.tglJatuhTempo.toString()
                }

                Text(
                    text = dateString,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
