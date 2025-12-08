package com.pln.monitoringpln.domain.repository

interface StorageRepository {
    suspend fun uploadTechnicianPhoto(byteArray: ByteArray, fileName: String): Result<String>
}
