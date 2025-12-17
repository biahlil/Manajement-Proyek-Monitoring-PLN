package com.pln.monitoringpln.presentation.report

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pln.monitoringpln.domain.model.ExportFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    state: ReportState,
    onStartDateChange: (Date) -> Unit,
    onEndDateChange: (Date) -> Unit,
    onFormatChange: (ExportFormat) -> Unit,
    onExport: () -> Unit,
    onBack: () -> Unit,
    onClearMessages: () -> Unit,
    onFullReportChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

    // Show Snackbar for messages
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.successMessage, state.errorMessage) {
        if (state.successMessage != null) {
            snackbarHostState.showSnackbar(state.successMessage)
            onClearMessages()
        }
        if (state.errorMessage != null) {
            snackbarHostState.showSnackbar(state.errorMessage)
            onClearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Laporan Tugas",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Card 1: Periode Laporan
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Pilih Periode Laporan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    // Full Report Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = state.isFullReport,
                            onCheckedChange = { onFullReportChange(it) },
                        )
                        Text(
                            text = "Download Full Database Report",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.clickable { onFullReportChange(!state.isFullReport) },
                        )
                    }

                    // Date Pickers (Disabled if Full Report)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Start Date
                        DatePickerField(
                            label = "Dari Tanggal",
                            date = state.startDate,
                            onDateSelected = onStartDateChange,
                            dateFormat = dateFormat,
                            modifier = Modifier.weight(1f),
                            enabled = !state.isFullReport,
                        )

                        // End Date
                        DatePickerField(
                            label = "Sampai Tanggal",
                            date = state.endDate,
                            onDateSelected = onEndDateChange,
                            dateFormat = dateFormat,
                            modifier = Modifier.weight(1f),
                            enabled = !state.isFullReport,
                        )
                    }
                }
            }

            // Card 2: Format Laporan
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Format Laporan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    // Format Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        FormatOption(
                            format = ExportFormat.PDF,
                            isSelected = state.format == ExportFormat.PDF,
                            onSelect = onFormatChange,
                            modifier = Modifier.weight(1f),
                        )
                        FormatOption(
                            format = ExportFormat.EXCEL_CSV,
                            isSelected = state.format == ExportFormat.EXCEL_CSV,
                            onSelect = onFormatChange,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Export Button
            Button(
                onClick = onExport,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !state.isLoading,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                    )
                } else {
                    Text("Unduh Laporan")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    label: String,
    date: Date,
    onDateSelected: (Date) -> Unit,
    dateFormat: SimpleDateFormat,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.time,
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Date(millis))
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

    OutlinedTextField(
        value = dateFormat.format(date),
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { if (enabled) showDatePicker = true }, enabled = enabled) {
                Icon(Icons.Default.DateRange, contentDescription = null)
            }
        },
        modifier = modifier.clickable(enabled = enabled) { showDatePicker = true },
        enabled = false, // Disable typing, handle click on parent
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.38f,
                )
            },
            disabledBorderColor = if (enabled) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.outline.copy(
                    alpha = 0.12f,
                )
            },
            disabledLabelColor = if (enabled) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.38f,
                )
            },
            disabledTrailingIconColor = if (enabled) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.38f,
                )
            },
        ),
    )
}

@Composable
fun FormatOption(
    format: ExportFormat,
    isSelected: Boolean,
    onSelect: (ExportFormat) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onSelect(format) },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(
                2.dp,
                MaterialTheme.colorScheme.primary,
            )
        } else {
            null
        },
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (format == ExportFormat.PDF) "PDF" else "CSV / Excel",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
