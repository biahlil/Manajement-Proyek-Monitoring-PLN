package com.pln.monitoringpln.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("role") val role: String,
    @SerialName("full_name") val fullName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("is_active") val isActive: Boolean? = true,
    @SerialName("avatar_url") val avatarUrl: String? = null
)
