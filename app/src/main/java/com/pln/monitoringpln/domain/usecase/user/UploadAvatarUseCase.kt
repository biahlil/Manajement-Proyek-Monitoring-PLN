package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.domain.repository.UserRepository

class UploadAvatarUseCase(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userId: String, byteArray: ByteArray): Result<String> {
        return userRepository.uploadAvatar(userId, byteArray)
    }
}
