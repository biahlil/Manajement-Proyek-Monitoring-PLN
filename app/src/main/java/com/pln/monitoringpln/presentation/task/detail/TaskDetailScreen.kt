package com.pln.monitoringpln.presentation.task.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    state: TaskDetailState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit
) {
    val context = LocalContext.current

    if (state.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text("Hapus Tugas") },
            text = { Text("Apakah Anda yakin ingin menghapus tugas ini?") },
            confirmButton = {
                TextButton(onClick = onConfirmDelete) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDelete) {
                    Text("Batal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Tugas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            if (state.isAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hapus", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.error, color = MaterialTheme.colorScheme.error)
            }
        } else {
            val task = state.task
            val equipment = state.equipment
            val technician = state.technician

            if (task != null && equipment != null && technician != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = task.deskripsi, // Using deskripsi as Title for now based on mock
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = when (task.status) {
                                        "In Progress" -> Color(0xFFFFF3E0)
                                        "Done" -> Color(0xFFE8F5E9)
                                        else -> Color(0xFFECEFF1)
                                    },
                                    contentColor = when (task.status) {
                                        "In Progress" -> Color(0xFFE65100)
                                        "Done" -> Color(0xFF1B5E20)
                                        else -> Color(0xFF455A64)
                                    }
                                ) {
                                    Text(
                                        text = task.status,
                                        style = MaterialTheme.typography.labelMedium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Teknisi: ${technician.namaLengkap}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Location Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Lokasi", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                Text(
                                    equipment.namaAlat, // Assuming location name is part of equipment or we use equipment name as location proxy
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:${equipment.latitude},${equipment.longitude}?q=${equipment.latitude},${equipment.longitude}(${equipment.namaAlat})")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Lihat Peta", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    // Specification / Details Section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Spesifikasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Divider()
                            
                            DetailRow("Teknisi", technician.namaLengkap)
                            DetailRow("Peralatan", equipment.namaAlat)
                            DetailRow("Kode Alat", equipment.kodeAlat)
                            DetailRow("Judul Tugas", task.deskripsi)
                            
                            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                            DetailRow("Deadline", dateFormat.format(task.tglJatuhTempo))
                            
                            DetailRow("Deskripsi", task.deskripsi) // Using deskripsi again as description
                        }
                    }

                    // Report Section (Laporan Pengerjaan)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Laporan Pengerjaan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Divider()

                            if (state.isAdmin) {
                                // Admin View (Read-Only)
                                DetailRow("Kondisi Alat", state.condition.ifBlank { "-" })
                                DetailRow("Status Alat", state.equipmentStatus)
                                DetailRow("Status Tugas", state.taskStatus)
                                
                                Text("Bukti Foto", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                if (state.proofUri != null) {
                                    // Placeholder for Image
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp)
                                            .background(Color.LightGray, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Foto Bukti Terlampir", color = Color.DarkGray)
                                    }
                                } else {
                                    Text("-", style = MaterialTheme.typography.bodyMedium)
                                }

                            } else {
                                // Technician View (Editable)
                                val viewModel: TaskDetailViewModel = org.koin.androidx.compose.koinViewModel() // Get VM instance for callbacks

                                // Kondisi Alat
                                OutlinedTextField(
                                    value = state.condition,
                                    onValueChange = viewModel::onConditionChange,
                                    label = { Text("Kondisi Alat") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3
                                )

                                // Status Alat
                                Text("Status Alat", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    listOf("Normal", "Rusak", "Perlu Perhatian").forEach { status ->
                                        FilterChip(
                                            selected = state.equipmentStatus == status,
                                            onClick = { viewModel.onEquipmentStatusChange(status) },
                                            label = { Text(status) },
                                            leadingIcon = if (state.equipmentStatus == status) {
                                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                            } else null
                                        )
                                    }
                                }

                                // Status Tugas (3-State Toggle)
                                Text("Status Tugas", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    listOf("To Do", "In Progress", "Finish").forEach { status ->
                                        ElevatedFilterChip(
                                            selected = state.taskStatus == status,
                                            onClick = { viewModel.onTaskStatusChange(status) },
                                            label = { Text(status) },
                                            colors = FilterChipDefaults.elevatedFilterChipColors(
                                                selectedContainerColor = when(status) {
                                                    "To Do" -> MaterialTheme.colorScheme.surfaceVariant
                                                    "In Progress" -> Color(0xFFFFF3E0) // Orange-ish
                                                    "Finish" -> Color(0xFFE8F5E9) // Green-ish
                                                    else -> MaterialTheme.colorScheme.primaryContainer
                                                },
                                                selectedLabelColor = when(status) {
                                                    "In Progress" -> Color(0xFFE65100)
                                                    "Finish" -> Color(0xFF1B5E20)
                                                    else -> MaterialTheme.colorScheme.onSurface
                                                }
                                            )
                                        )
                                    }
                                }

                                // Upload Bukti
                                Button(
                                    onClick = { viewModel.onProofSelected("mock_uri") }, // Mock image selection
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null) // Use Camera/Upload icon
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (state.proofUri != null) "Ganti Foto Bukti" else "Upload Bukti Foto")
                                }
                                
                                if (state.proofUri != null) {
                                    Text("Foto berhasil dipilih", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }

                                // Simpan Button
                                Button(
                                    onClick = viewModel::onSaveReport,
                                    modifier = Modifier.fillMaxWidth().height(50.dp),
                                    enabled = !state.isSavingReport
                                ) {
                                    if (state.isSavingReport) {
                                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                    } else {
                                        Text("Simpan Laporan")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}
