package com.pln.monitoringpln.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val namaLengkap: String,
    val role: String,
    val isActive: Boolean = true,
    val photoUrl: String? = null
)
