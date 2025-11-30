package com.pln.monitoringpln.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pln.monitoringpln.R
import com.pln.monitoringpln.domain.model.DashboardSummary
import com.pln.monitoringpln.presentation.theme.MonitoringPLNTheme

@Composable
@Composable
fun DashboardScreen(
    state: DashboardState,
    onEquipmentClick: () -> Unit,
    onTechnicianClick: () -> Unit,
    onTaskClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp) // Space for BottomNav
    ) {
        // Welcome Section
        item {
            WelcomeSection(
                isAdmin = state.isAdmin,
                taskCount = state.technicianTasks.size // Or summary.tugasToDo if preferred
            )
        }

        // Stats Grid
        // Stats Grid
        item {
            StatsGrid(
                summary = state.summary, 
                isAdmin = state.isAdmin, 
                onEquipmentClick = onEquipmentClick,
                onTaskClick = onTaskClick
            )
        }

        // Admin Specific Sections
        if (state.isAdmin) {
            item {
                SectionTitle(title = "Tugas In Progress")
            }
            // Mock items for now, later bind to state.inProgressTasks
            items(3) {
                TaskItemMock()
            }

            item {
                TechnicianStatusSection(
                    totalTeknisi = state.summary.totalTeknisi,
                    onClick = onTechnicianClick
                )
            }
        } else {
            // Technician Specific Sections
            if (state.activeWarnings.isNotEmpty()) {
                item {
                    SectionTitle(title = "Peringatan Aktif")
                }
                items(state.activeWarnings) { task ->
                    TaskItemMock(title = task.deskripsi, status = "Warning", isWarning = true)
                }
            }
            
            item {
                SectionTitle(title = "Tugas Hari Ini")
            }
            items(state.technicianTasks) { task ->
                TaskItemMock(title = task.deskripsi, status = "To Do")
            }
        }
    }
}



@Composable
fun WelcomeSection(isAdmin: Boolean, taskCount: Int = 0) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (isAdmin) "DASHBOARD ADMINISTRATOR" else "DASHBOARD TEKNISI",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isAdmin) 
                    "Selamat Datang Admin, Selamat Bekerja!" 
                else 
                    "Selamat Datang Teknisi, Anda punya $taskCount tugas yang belum dikerjakan hari ini!",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun StatsGrid(
    summary: DashboardSummary, 
    isAdmin: Boolean, 
    onEquipmentClick: () -> Unit,
    onTaskClick: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                title = "Total Alat",
                value = summary.totalAlat.toString(),
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = onEquipmentClick
            )
            
            if (isAdmin) {
                StatCard(
                    title = "Normal",
                    value = summary.totalAlatNormal.toString(),
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFFE6F4EA), // Light Green
                    contentColor = Color(0xFF137333),
                    onClick = onEquipmentClick
                )
            } else {
                StatCard(
                    title = "Total Tugas",
                    value = summary.totalTugas.toString(),
                    modifier = Modifier.weight(1f),
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = onTaskClick
                )
            }
        }
        
        if (isAdmin) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Perlu Perhatian",
                    value = summary.totalAlatPerluPerhatian.toString(),
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFFFEF7E0), // Light Yellow
                    contentColor = Color(0xFFB06000),
                    onClick = onEquipmentClick
                )
                StatCard(
                    title = "Rusak",
                    value = summary.totalAlatRusak.toString(),
                    modifier = Modifier.weight(1f),
                    containerColor = Color(0xFFFCE8E6), // Light Red
                    contentColor = Color(0xFFC5221F),
                    onClick = onEquipmentClick
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String, 
    value: String, 
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun TaskItemMock(
    title: String = "Perbaikan Gardu A",
    status: String = "In Progress",
    isWarning: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isWarning) MaterialTheme.colorScheme.errorContainer 
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                 Icon(
                     imageVector = Icons.Default.Notifications, // Placeholder icon
                     contentDescription = "Task Icon",
                     tint = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                 )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold)
                Text(
                    text = "Status: $status", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = if (isWarning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun TechnicianStatusSection(totalTeknisi: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status Teknisi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "Total: $totalTeknisi",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sample List
            repeat(3) {
                TechnicianItemMock()
            }
        }
    }
}

@Composable
fun TechnicianItemMock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = "Nama Teknisi", fontWeight = FontWeight.Bold)
            Text(text = "Status: Sedang Bertugas", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MonitoringPLNTheme {
        DashboardScreen(state = DashboardState(isAdmin = true))
    }
}
