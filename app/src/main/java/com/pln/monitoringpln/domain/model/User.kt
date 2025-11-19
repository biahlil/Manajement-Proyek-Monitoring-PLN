package com.pln.monitoringpln.domain.model

data class User(
    val id: String, // ID dari Supabase Auth
    val email: String,
    val namaLengkap: String,
    val role: String, // "Admin" atau "Teknisi"
)
