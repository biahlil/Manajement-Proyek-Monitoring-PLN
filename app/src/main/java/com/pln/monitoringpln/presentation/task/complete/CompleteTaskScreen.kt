package com.pln.monitoringpln.presentation.task.complete

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pln.monitoringpln.presentation.task.detail.LocationCard
import com.pln.monitoringpln.presentation.task.detail.SpecificationCard
import com.pln.monitoringpln.presentation.task.detail.TaskHeaderCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteTaskScreen(
    state: CompleteTaskState,
    onBack: () -> Unit,
    onConditionChange: (String) -> Unit,
    onEquipmentStatusChange: (String) -> Unit,
    onProofSelected: (String) -> Unit,
    onCompleteTask: (ByteArray?) -> Unit,
) {
    val context = LocalContext.current

    // Success Navigation
    LaunchedEffect(state.isCompleted) {
        if (state.isCompleted) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selesaikan Tugas", fontWeight = FontWeight.Bold) },
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
                    TaskHeaderCard(task, technician)
                    LocationCard(equipment, context)
                    SpecificationCard(task, equipment, technician)

                    // Report Form
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            2.dp,
                            MaterialTheme.colorScheme.outlineVariant,
                        ),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Text(
                                "Laporan Pengerjaan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            HorizontalDivider()

                            // Kondisi Alat
                            OutlinedTextField(
                                value = state.condition,
                                onValueChange = onConditionChange,
                                label = { Text("Kondisi Alat") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                isError = state.conditionError != null,
                                supportingText = {
                                    if (state.conditionError != null) {
                                        Text(text = state.conditionError, color = MaterialTheme.colorScheme.error)
                                    }
                                },
                            )

                            // Status Alat (Chips)
                            Text(
                                "Status Alat",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf("Normal", "Rusak", "Perlu Perhatian").forEach { status ->
                                    FilterChip(
                                        selected = state.equipmentStatus == status,
                                        onClick = { onEquipmentStatusChange(status) },
                                        label = { Text(status) },
                                        leadingIcon = if (state.equipmentStatus == status) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp),
                                                )
                                            }
                                        } else {
                                            null
                                        },
                                    )
                                }
                            }

                            // Upload Bukti
                            Text(
                                "Bukti Foto",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                            )

                            val photoPickerLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.PickVisualMedia(),
                            ) { uri: Uri? ->
                                if (uri != null) {
                                    onProofSelected(uri.toString())
                                }
                            }

                            if (!state.proofUri.isNullOrBlank()) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    AsyncImage(
                                        model = state.proofUri,
                                        contentDescription = "Preview Bukti",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop,
                                    )
                                    IconButton(
                                        onClick = { onProofSelected("") }, // Clear photo
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .background(
                                                Color.Black.copy(alpha = 0.5f),
                                                androidx.compose.foundation.shape.CircleShape,
                                            ),
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Hapus Foto",
                                            tint = Color.White,
                                        )
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Upload Foto Bukti")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Simpan Button
                            Button(
                                onClick = {
                                    val uriString = state.proofUri
                                    if (!uriString.isNullOrBlank()) {
                                        if (uriString.startsWith("http")) {
                                            // Existing remote URL, no need to upload bytes
                                            onCompleteTask(null)
                                        } else {
                                            // New local URI, read bytes
                                            try {
                                                val uri = Uri.parse(uriString)
                                                val inputStream = context.contentResolver.openInputStream(uri)
                                                val bytes = inputStream?.readBytes()
                                                inputStream?.close()
                                                onCompleteTask(bytes)
                                            } catch (e: Exception) {
                                                // Handle error reading file
                                                onCompleteTask(null) // Will fail validation in VM
                                            }
                                        }
                                    } else {
                                        onCompleteTask(null)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                enabled = !state.isSaving,
                            ) {
                                if (state.isSaving) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Text("Selesaikan Tugas")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
