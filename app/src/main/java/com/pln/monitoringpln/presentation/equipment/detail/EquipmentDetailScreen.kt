package com.pln.monitoringpln.presentation.equipment.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetailScreen(
    state: EquipmentDetailState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onAddTask: () -> Unit,
    onTaskClick: (String) -> Unit,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Alat", fontWeight = FontWeight.Bold) },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
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

                    Button(
                        onClick = onEdit,
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
        },
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.alatHistory != null) {
            val equipment = state.alatHistory.alat
            val history = state.alatHistory.riwayatTugas
            val lastMaintenance = history.firstOrNull { it.status == "Done" }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Header
                item {
                    DetailHeader(equipment, lastMaintenance)
                }

                // Warning (Conditional)
                if (equipment.status != "Normal") {
                    item {
                        WarningCard(equipment)
                    }
                }

                // Specifications
                item {
                    SpecificationsCard(
                        equipment = equipment,
                        onMapClick = {
                            val uri =
                                Uri.parse("geo:${equipment.latitude},${equipment.longitude}?q=${equipment.latitude},${equipment.longitude}(${equipment.namaAlat})")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)

                            try {
                                context.startActivity(mapIntent)
                            } catch (e: android.content.ActivityNotFoundException) {
                                // Fallback to browser if no map app is installed
                                val browserUri =
                                    Uri.parse("https://www.google.com/maps/search/?api=1&query=${equipment.latitude},${equipment.longitude}")
                                val browserIntent = Intent(Intent.ACTION_VIEW, browserUri)
                                try {
                                    context.startActivity(browserIntent)
                                } catch (e2: Exception) {
                                    // Fallback if even browser fails (very unlikely)
                                    android.widget.Toast.makeText(
                                        context,
                                        "Tidak ada aplikasi untuk membuka peta",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                    )
                }

                // History
                item {
                    Text(
                        text = "Riwayat Inspeksi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }

                if (history.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada riwayat inspeksi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                } else {
                    items(history.size) { index ->
                        HistoryItem(
                            tugas = history[index],
                            onClick = { onTaskClick(history[index].id) },
                        )
                    }
                }
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error)
            }
        }

        // Delete Dialog
        if (state.showDeleteDialog) {
            AlertDialog(
                onDismissRequest = onDismissDelete,
                title = { Text("Hapus Alat") },
                text = { Text("Apakah Anda yakin ingin menghapus ${state.alatHistory?.alat?.namaAlat}?") },
                confirmButton = {
                    TextButton(
                        onClick = onConfirmDelete,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    ) {
                        if (state.isDeleting) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Hapus")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = onDismissDelete) {
                        Text("Batal")
                    }
                },
            )
        }
    }
}

@Composable
fun DetailHeader(equipment: Alat, lastMaintenance: Tugas?) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val lastMaintenanceDate = lastMaintenance?.tglJatuhTempo?.let { dateFormat.format(it) } ?: "Belum ada"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)), // Light Purple
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equipment.namaAlat,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF311B92), // Dark Purple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = equipment.kodeAlat,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5E35B1), // Medium Purple
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = equipment.tipe,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575),
                    )
                }

                // Status Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = when (equipment.status) {
                        "Normal" -> Color(0xFFE8F5E9)
                        "Rusak" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFFFF3E0)
                    },
                    contentColor = when (equipment.status) {
                        "Normal" -> Color(0xFF1B5E20)
                        "Rusak" -> Color(0xFFB71C1C)
                        else -> Color(0xFFE65100)
                    },
                ) {
                    Text(
                        text = equipment.status,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Pemeliharaan Terakhir: $lastMaintenanceDate",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF757575),
            )
        }
    }
}

@Composable
fun WarningCard(equipment: Alat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), // Light Red
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFB71C1C), // Red
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Perhatian Diperlukan",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB71C1C),
                )
                Text(
                    text = "Kondisi alat ${equipment.kondisi}. Segera lakukan pengecekan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB71C1C),
                )
            }
        }
    }
}

@Composable
fun SpecificationsCard(equipment: Alat, onMapClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Lokasi",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // Map View
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp),
                ),
        ) {
            // Map Component
            com.pln.monitoringpln.presentation.components.MapPicker(
                modifier = Modifier.fillMaxSize(),
                initialLocation = org.osmdroid.util.GeoPoint(equipment.latitude, equipment.longitude),
                onLocationSelected = { _, _ -> }, // No-op for read-only view
            )

            // Transparent Overlay for Click
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .clickable(onClick = onMapClick),
            )
        }
    }
}

@Composable
fun SpecItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun HistoryItem(tugas: Tugas, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val dateStr = dateFormat.format(tugas.tglDibuat)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            if (tugas.status == "Done") Icons.Default.CheckCircle else Icons.Default.Info,
            contentDescription = null,
            tint = if (tugas.status == "Done") Color(0xFF4CAF50) else Color(0xFFFFC107),
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = tugas.judul,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = "$dateStr • Status: ${tugas.status}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
    }
}
