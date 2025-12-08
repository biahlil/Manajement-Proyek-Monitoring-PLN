package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.StorageRepository

class FakeStorageRepository : StorageRepository {

    var shouldFail = false

    override suspend fun uploadTechnicianPhoto(byteArray: ByteArray, fileName: String): Result<String> {
        if (shouldFail) {
            return Result.failure(Exception("Upload failed"))
        }
        return Result.success("https://dummy.url/$fileName")
    }
}
