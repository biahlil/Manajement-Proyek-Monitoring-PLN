package com.pln.monitoringpln.domain.model

data class User(
    val id: String,
    val email: String,
    val namaLengkap: String,
    val role: String, // "Admin" atau "Teknisi"
    val isActive: Boolean = true // Default aktif
)