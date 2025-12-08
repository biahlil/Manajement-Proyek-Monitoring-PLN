package com.pln.monitoringpln.presentation.task.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User
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
    onDismissDelete: () -> Unit,
    onCompleteTask: () -> Unit,
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
            },
        )
    }

    // Success Navigation
    LaunchedEffect(state.isReportSaved, state.isDeleted) {
        if (state.isReportSaved || state.isDeleted) {
            onBack()
        }
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
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        bottomBar = {
            if (state.isAdmin) {
                TaskActionBottomBar(
                    onMainAction = onEdit,
                    mainActionText = "Edit",
                    mainActionIcon = Icons.Default.Edit,
                    onDelete = onDelete,
                    canDelete = true,
                )
            } else {
                // For Technicians, "Edit" becomes "Selesaikan" (Complete)
                // Only show if task is not done? Or allow editing report?
                // User request: "edit tugas untuk teknisi menjadi ke complete tugas"
                TaskActionBottomBar(
                    onMainAction = onCompleteTask,
                    mainActionText = if (state.task?.status == "Done") "Edit Laporan" else "Selesaikan",
                    mainActionIcon = Icons.Default.Check,
                    onDelete = onDelete,
                    canDelete = false,
                )
            }
        },
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
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    if (state.isAdmin) {
                        AdminTaskDetailContent(
                            task = task,
                            equipment = equipment,
                            technician = technician,
                            state = state,
                            context = context,
                        )
                    } else {
                        TechnicianTaskDetailContent(
                            task = task,
                            equipment = equipment,
                            technician = technician,
                            state = state,
                            context = context,
                            onCompleteTask = onCompleteTask,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskActionBottomBar(
    onMainAction: () -> Unit,
    mainActionText: String,
    mainActionIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onDelete: () -> Unit,
    canDelete: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (canDelete) {
            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hapus", fontWeight = FontWeight.Bold)
            }
        }

        Button(
            onClick = onMainAction,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AdminTaskDetailContent(
    task: Tugas,
    equipment: Alat,
    technician: User,
    state: TaskDetailState,
    context: Context,
) {
    TaskHeaderCard(task, technician)
    LocationCard(equipment, context)
    SpecificationCard(task, equipment, technician)

    // Report Section (Read-Only)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Laporan Pengerjaan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            DetailRow("Kondisi Alat", state.condition.ifBlank { "-" })
            DetailRow("Status Alat", state.equipmentStatus)
            DetailRow("Status Tugas", state.taskStatus)

            Text("Bukti Foto", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            if (state.proofUri != null) { // Note: state.proofUri comes from task.buktiFoto in ViewModel
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.LightGray)
                            .clickable {
                                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText("Bukti Foto", state.proofUri)
                                clipboardManager.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Link foto disalin", android.widget.Toast.LENGTH_SHORT).show()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = state.proofUri,
                            contentDescription = "Bukti Foto",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Klik foto untuk salin link",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                    )
                }
            } else {
                Text("-", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun TechnicianTaskDetailContent(
    task: Tugas,
    equipment: Alat,
    technician: User,
    state: TaskDetailState,
    context: Context,
    onCompleteTask: () -> Unit,
) {
    TaskHeaderCard(task, technician)
    LocationCard(equipment, context)
    SpecificationCard(task, equipment, technician)

    // Report Section (Read-Only if done, or just info)
    // If task is not done, the action is now in the bottom bar.
    // So we just show the report status or nothing if not done yet.

    if (task.status == "Done") {
        // Show Completed Report (Read-Only)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Laporan Pengerjaan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                HorizontalDivider()

                DetailRow("Kondisi Alat", task.kondisiAkhir ?: "-")
                DetailRow("Status Tugas", task.status)

                Text("Bukti Foto", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                if (task.buktiFoto != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .clickable {
                                    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                    val clip = android.content.ClipData.newPlainText("Bukti Foto", task.buktiFoto)
                                    clipboardManager.setPrimaryClip(clip)
                                    android.widget.Toast.makeText(context, "Link foto disalin", android.widget.Toast.LENGTH_SHORT).show()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            AsyncImage(
                                model = task.buktiFoto,
                                contentDescription = "Bukti Foto",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "Klik foto untuk salin link",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                        )
                    }
                } else {
                    Text("-", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun TaskHeaderCard(task: Tugas, technician: User) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = task.judul,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
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
                    },
                ) {
                    Text(
                        text = task.status,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Teknisi: ${technician.namaLengkap}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun LocationCard(equipment: Alat, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Lokasi", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(
                    equipment.namaAlat,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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
                modifier = Modifier.height(32.dp),
            ) {
                Text("Lihat Peta", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun SpecificationCard(task: Tugas, equipment: Alat, technician: User) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Spesifikasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            HorizontalDivider()

            DetailRow("Teknisi", technician.namaLengkap)
            DetailRow("Peralatan", equipment.namaAlat)
            DetailRow("Kode Alat", equipment.kodeAlat)
            DetailRow("Judul Tugas", task.judul)

            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            DetailRow("Deadline", dateFormat.format(task.tglJatuhTempo))

            DetailRow("Deskripsi", task.deskripsi)
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
        )
    }
}
