package com.pln.monitoringpln.domain.usecase.storage

import com.pln.monitoringpln.domain.repository.StorageRepository

class UploadPhotoUseCase(
    private val storageRepository: StorageRepository
) {
    suspend operator fun invoke(byteArray: ByteArray, fileName: String): Result<String> {
        return storageRepository.uploadTechnicianPhoto(byteArray, fileName)
    }
}
