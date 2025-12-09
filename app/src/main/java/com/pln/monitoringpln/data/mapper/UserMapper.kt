package com.pln.monitoringpln.data.mapper

import com.pln.monitoringpln.data.local.entity.UserEntity
import com.pln.monitoringpln.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        email = email,
        namaLengkap = namaLengkap,
        role = role,
        photoUrl = photoUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        email = email,
        namaLengkap = namaLengkap,
        role = role,
        isActive = true,
        photoUrl = photoUrl,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
