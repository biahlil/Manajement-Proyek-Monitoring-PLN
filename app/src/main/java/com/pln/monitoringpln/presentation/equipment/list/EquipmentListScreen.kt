package com.pln.monitoringpln.presentation.equipment.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pln.monitoringpln.domain.model.Alat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    state: EquipmentListState,
    onSearchQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onAddEquipment: () -> Unit,
    onDeleteEquipment: (Alat) -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    onItemClick: (Alat) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daftar Peralatan",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            if (state.isAdmin) {
                FloatingActionButton(
                    onClick = onAddEquipment,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Equipment")
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari nama atau kode alat...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Total Chip
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
            ) {
                Text(
                    text = "Total Peralatan: ${state.equipmentList.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // List
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 80.dp), // Space for FAB
                ) {
                    items(state.filteredEquipmentList) { equipment ->
                        EquipmentCard(
                            equipment = equipment,
                            isAdmin = state.isAdmin,
                            onDeleteClick = { onDeleteEquipment(equipment) },
                            onClick = { onItemClick(equipment) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: Alat,
    isAdmin: Boolean,
    onDeleteClick: () -> Unit,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Name and Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = equipment.namaAlat,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = equipment.kodeAlat,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
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
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = equipment.locationName ?: "Lokasi belum tersedia",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
