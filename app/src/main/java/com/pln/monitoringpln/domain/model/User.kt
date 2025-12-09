package com.pln.monitoringpln.domain.model

data class User(
    val id: String,
    val email: String,
    val namaLengkap: String,
    val role: String, // "Admin" atau "Teknisi"
    val isActive: Boolean = true, // Default aktif
    val photoUrl: String? = null,
    val createdAt: java.util.Date? = null,
    val updatedAt: java.util.Date? = null,
)
