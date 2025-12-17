package com.pln.monitoringpln.presentation.task.addedit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.User
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    state: AddEditTaskState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onEquipmentSearchQueryChange: (String) -> Unit,
    onEquipmentSelected: (Alat) -> Unit,
    onDeadlineSelected: (LocalDate) -> Unit,
    onTechnicianSelected: (User) -> Unit,
    onSaveTask: () -> Unit,
    onBack: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTechnicianDropdown by remember { mutableStateOf(false) }
    var showEquipmentDropdown by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDeadlineSelected(date)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.taskId != null) "Edit Tugas" else "Tambah Tugas",
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
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Batal", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSaveTask,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    enabled = !state.isSaving,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Simpan Tugas", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Error Message
            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("Judul Tugas *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                isError = state.titleError != null,
                supportingText = {
                    if (state.titleError != null) {
                        Text(text = state.titleError, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorSupportingTextColor = MaterialTheme.colorScheme.error,
                ),
            )

            // Equipment (Searchable Dropdown)
            ExposedDropdownMenuBox(
                expanded = showEquipmentDropdown,
                onExpandedChange = { showEquipmentDropdown = !showEquipmentDropdown },
            ) {
                OutlinedTextField(
                    value = state.equipmentSearchQuery,
                    onValueChange = {
                        onEquipmentSearchQueryChange(it)
                        showEquipmentDropdown = true
                    },
                    label = { Text("Pilih Peralatan *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showEquipmentDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                )
                ExposedDropdownMenu(
                    expanded = showEquipmentDropdown,
                    onDismissRequest = { showEquipmentDropdown = false },
                ) {
                    state.availableEquipments.forEach { equipment ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(equipment.namaAlat, fontWeight = FontWeight.Bold)
                                    Text(
                                        equipment.kodeAlat,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                    )
                                }
                            },
                            onClick = {
                                onEquipmentSelected(equipment)
                                showEquipmentDropdown = false
                            },
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = state.description,
                onValueChange = onDescriptionChange,
                label = { Text("Deskripsi Tugas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
                maxLines = 5,
                isError = state.descriptionError != null,
                supportingText = {
                    if (state.descriptionError != null) {
                        Text(text = state.descriptionError, color = MaterialTheme.colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = MaterialTheme.colorScheme.error,
                    errorLabelColor = MaterialTheme.colorScheme.error,
                    errorSupportingTextColor = MaterialTheme.colorScheme.error,
                ),
            )

            // Deadline
            OutlinedTextField(
                value = state.deadline?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "",
                onValueChange = {},
                label = { Text("Deadline *") },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                shape = RoundedCornerShape(8.dp),
                enabled = false, // Disable typing, force click
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            )

            // Technician
            ExposedDropdownMenuBox(
                expanded = showTechnicianDropdown,
                onExpandedChange = { showTechnicianDropdown = !showTechnicianDropdown },
            ) {
                OutlinedTextField(
                    value = state.selectedTechnician?.namaLengkap ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pilih Teknisi *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTechnicianDropdown) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(8.dp),
                )
                ExposedDropdownMenu(
                    expanded = showTechnicianDropdown,
                    onDismissRequest = { showTechnicianDropdown = false },
                ) {
                    state.availableTechnicians.forEach { technician ->
                        DropdownMenuItem(
                            text = { Text(technician.namaLengkap) },
                            onClick = {
                                onTechnicianSelected(technician)
                                showTechnicianDropdown = false
                            },
                        )
                    }
                }
            }
        }
    }
}
