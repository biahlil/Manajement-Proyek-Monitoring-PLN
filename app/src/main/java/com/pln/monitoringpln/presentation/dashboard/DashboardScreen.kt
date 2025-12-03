package com.pln.monitoringpln.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.pln.monitoringpln.R
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.presentation.components.BottomNavigationBar
import com.pln.monitoringpln.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

import com.pln.monitoringpln.presentation.theme.StatusGreenContainer
import com.pln.monitoringpln.presentation.theme.StatusGreenContent
import com.pln.monitoringpln.presentation.theme.StatusPurpleContainer
import com.pln.monitoringpln.presentation.theme.StatusPurpleContent
import com.pln.monitoringpln.presentation.theme.StatusRedContainer
import com.pln.monitoringpln.presentation.theme.StatusRedContent
import com.pln.monitoringpln.presentation.theme.StatusYellowContainer
import com.pln.monitoringpln.presentation.theme.StatusYellowContent

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                WelcomeCard(isAdmin = state.isAdmin)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (state.isAdmin) {
                    Button(
                        onClick = { onNavigate(Screen.Report.route) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Icon(imageVector = androidx.compose.material.icons.Icons.Filled.DateRange, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Unduh Full Laporan")
                    }
                }
            }

            item {
                StatGrid(
                    state = state,
                    onCardClick = { type ->
                        if (type == "my_tasks" || type == "all_tasks") {
                            onNavigate(Screen.TaskList.route)
                        } else {
                            onNavigate(Screen.EquipmentList.createRoute(type))
                        }
                    }
                )
            }

            item {
                Text(
                    text = if (state.isAdmin) "Tugas In Progress" else "Tugas Hari Ini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            val tasks = if (state.isAdmin) state.inProgressTasks else state.technicianTasks
            if (tasks.isEmpty()) {
                item {
                    Text(
                        text = "Tidak ada tugas saat ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onClick = { onNavigate(Screen.TaskList.route) }
                    )
                }
            }

            if (state.isAdmin && state.technicians.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Teknisi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                if (state.isTechniciansLoading && state.technicians.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                } else {
                    items(state.technicians) { technician ->
                        TechnicianItem(
                            user = technician, 
                            onClick = { onNavigate(Screen.TechnicianList.route) }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}



@Composable
fun WelcomeCard(isAdmin: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isAdmin) "DASHBOARD ADMINISTRATOR" else "DASHBOARD TEKNISI",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAdmin)
                    "Selamat Bekerja, Admin!"
                else
                    "Halo Teknisi, cek tugasmu hari ini.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun StatGrid(state: DashboardState, onCardClick: (String) -> Unit) {
    if (state.isAdmin) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Total Tugas Card (Full Width)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    title = "Total Alat",
                    count = state.summary.totalAlat.toString(),
                    containerColor = StatusPurpleContainer,
                    contentColor = StatusPurpleContent,
                    modifier = Modifier.weight(1f),
                    onClick = { onCardClick("all_equipment") }
                )
                StatCard(
                    title = "Normal",
                    count = state.summary.totalAlatNormal.toString(),
                    containerColor = StatusGreenContainer,
                    contentColor = StatusGreenContent,
                    modifier = Modifier.weight(1f),
                    onClick = { onCardClick("normal_equipment") }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatCard(
                    title = "Perlu Perhatian",
                    count = state.summary.totalAlatPerluPerhatian.toString(),
                    containerColor = StatusYellowContainer,
                    contentColor = StatusYellowContent,
                    modifier = Modifier.weight(1f),
                    onClick = { onCardClick("warning_equipment") }
                )
                StatCard(
                    title = "Rusak",
                    count = state.summary.totalAlatRusak.toString(),
                    containerColor = StatusRedContainer,
                    contentColor = StatusRedContent,
                    modifier = Modifier.weight(1f),
                    onClick = { onCardClick("broken_equipment") }
                )
            }
        }
    } else {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                title = "Total Alat",
                count = state.summary.totalAlat.toString(),
                containerColor = StatusPurpleContainer,
                contentColor = StatusPurpleContent,
                modifier = Modifier.weight(1f),
                onClick = { onCardClick("my_equipment") }
            )
            StatCard(
                title = "Total Tugas",
                count = state.summary.totalTugas.toString(),
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
                onClick = { onCardClick("my_tasks") }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    count: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
fun TaskItem(task: Tugas, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = task.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Status: ${task.status.replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }.split(" ").joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.ROOT) else char.toString() } }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}



