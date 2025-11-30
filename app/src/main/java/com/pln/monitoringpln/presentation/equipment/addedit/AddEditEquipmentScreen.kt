package com.pln.monitoringpln.presentation.equipment.addedit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditEquipmentScreen(
    state: AddEditEquipmentState,
    onNamaChange: (String) -> Unit,
    onKodeChange: (String) -> Unit,
    onTipeChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onLokasiChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    var expandedStatus by remember { mutableStateOf(false) }
    val statusOptions = listOf("Normal", "Rusak", "Perlu Perhatian")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (state.isEditMode) "Edit Alat" else "Tambah Alat", 
                        fontWeight = FontWeight.Bold
                    ) 
                },
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
        }
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Nama Peralatan
                OutlinedTextField(
                    value = state.namaAlat,
                    onValueChange = onNamaChange,
                    label = { Text("Nama Peralatan *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Kode Alat
                OutlinedTextField(
                    value = state.kodeAlat,
                    onValueChange = onKodeChange,
                    label = { Text("Kode Alat *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Tipe Peralatan
                OutlinedTextField(
                    value = state.tipePeralatan,
                    onValueChange = onTipeChange,
                    label = { Text("Tipe Peralatan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedStatus,
                    onExpandedChange = { expandedStatus = !expandedStatus },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedStatus,
                        onDismissRequest = { expandedStatus = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    onStatusChange(option)
                                    expandedStatus = false
                                }
                            )
                        }
                    }
                }

                // Lokasi
                OutlinedTextField(
                    value = state.lokasi,
                    onValueChange = onLokasiChange,
                    label = { Text("Lokasi") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Error Message
                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Save Button
                Button(
                    onClick = onSave,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}
