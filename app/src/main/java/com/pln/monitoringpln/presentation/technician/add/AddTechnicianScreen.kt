package com.pln.monitoringpln.presentation.technician.add

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTechnicianScreen(
    state: AddTechnicianState,
    onNamaChange: (String) -> Unit,
    onIdChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onNoTeleponChange: (String) -> Unit,
    onAreaTugasChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Teknisi", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nama Lengkap
            OutlinedTextField(
                value = state.namaLengkap,
                onValueChange = onNamaChange,
                label = { Text("Nama Lengkap *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // ID Teknisi
            OutlinedTextField(
                value = state.idTeknisi,
                onValueChange = onIdChange,
                label = { Text("ID Teknisi *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Email
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = { Text("Email *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // No Telepon
            OutlinedTextField(
                value = state.noTelepon,
                onValueChange = onNoTeleponChange,
                label = { Text("No Telepon *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Area Tugas
            OutlinedTextField(
                value = state.areaTugas,
                onValueChange = onAreaTugasChange,
                label = { Text("Area Tugas") },
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
