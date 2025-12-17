package com.pln.monitoringpln.presentation.equipment.addedit

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEquipmentScreen(
    state: AddEditEquipmentState,
    onNamaChange: (String) -> Unit,
    // onKodeChange removed
    onTipeChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onLokasiChange: (String) -> Unit,
    onLatChange: (String) -> Unit,
    onLngChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    viewModel: androidx.lifecycle.ViewModel? = null, // Optional for Geocoding access
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollState = rememberScrollState()
    var expandedStatus by remember { mutableStateOf(false) }
    val statusOptions = listOf("Normal", "Rusak", "Perlu Perhatian")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.isEditMode) "Edit Alat" else "Tambah Alat",
                        fontWeight = FontWeight.Bold,
                    )
                },
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Nama Peralatan
                OutlinedTextField(
                    value = state.namaAlat,
                    onValueChange = onNamaChange,
                    label = { Text("Nama Peralatan *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.namaAlatError != null,
                    supportingText = {
                        if (state.namaAlatError != null) {
                            Text(
                                text = state.namaAlatError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error,
                    ),
                )

                // Kode Alat removed (Auto-generated)

                // Tipe Peralatan
                OutlinedTextField(
                    value = state.tipePeralatan,
                    onValueChange = onTipeChange,
                    label = { Text("Tipe Peralatan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.tipeError != null,
                    supportingText = {
                        if (state.tipeError != null) {
                            Text(
                                text = state.tipeError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error,
                    ),
                )

                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = state.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false },
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onStatusChange(option)
                                    expandedStatus = false
                                },
                            )
                        }
                    }
                }

                // Kondisi (Description)
                OutlinedTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Kondisi (Deskripsi)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    isError = state.descriptionError != null,
                    supportingText = {
                        if (state.descriptionError != null) {
                            Text(
                                text = state.descriptionError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error,
                        errorSupportingTextColor = MaterialTheme.colorScheme.error,
                    ),
                )

                // Selected Location Display
                Text(
                    text = if (state.latitude != 0.0 && state.longitude != 0.0) {
                        "Lokasi Terpilih: ${state.latitude}, ${state.longitude}"
                    } else {
                        "Belum ada lokasi yang dipilih"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Pilih Lokasi di Peta:", style = MaterialTheme.typography.labelLarge)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(vertical = 8.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .clip(RoundedCornerShape(4.dp)),
                ) {
                    com.pln.monitoringpln.presentation.components.MapPicker(
                        modifier = Modifier.fillMaxSize(),
                        initialLocation = if (state.latitude != 0.0 && state.longitude != 0.0) {
                            org.osmdroid.util.GeoPoint(state.latitude, state.longitude)
                        } else {
                            null
                        },
                        onLocationSelected = { lat, lng ->
                            onLatChange(lat.toString())
                            onLngChange(lng.toString())
                            // Trigger Reverse Geocoding
                            (viewModel as? AddEditEquipmentViewModel)?.updateLocationName(context, lat, lng)
                        },
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message
                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                // Save Button
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !state.isSaving,
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    } else {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}
