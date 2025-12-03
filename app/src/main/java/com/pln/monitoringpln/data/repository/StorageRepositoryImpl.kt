package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.domain.repository.StorageRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StorageRepositoryImpl(
    private val supabaseClient: SupabaseClient
) : StorageRepository {

    override suspend fun uploadTechnicianPhoto(byteArray: ByteArray, fileName: String): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val bucket = supabaseClient.storage.from("avatars")
                bucket.upload(fileName, byteArray, upsert = true)
                val url = bucket.publicUrl(fileName)
                Result.success(url)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
