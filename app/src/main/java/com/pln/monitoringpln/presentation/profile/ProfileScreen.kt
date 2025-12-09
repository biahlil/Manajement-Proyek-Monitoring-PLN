package com.pln.monitoringpln.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProfileScreen(
    state: ProfileState,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onEditProfile,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Avatar Section
            ProfileAvatar(state)

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Details
            ProfileDetailsCard(state)

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Keluar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ProfileAvatar(state: ProfileState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Avatar Image Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                if (state.photoUrl != null) {
                    coil.compose.AsyncImage(
                        model = state.photoUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = state.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = state.role,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun ProfileDetailsCard(state: ProfileState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            ProfileDetailItem(label = "Username", value = state.username)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            ProfileDetailItem(label = "Email", value = state.email)
        }
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
        )
    }
}
