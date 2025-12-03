package com.pln.monitoringpln.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pln.monitoringpln.domain.repository.StorageRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StorageRepositoryImplTest {

    private lateinit var supabaseClient: SupabaseClient
    private lateinit var storageRepository: StorageRepository

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        // Initialize real Supabase Client for Integration Test
        supabaseClient = createSupabaseClient(
            supabaseUrl = com.pln.monitoringpln.BuildConfig.SUPABASE_URL,
            supabaseKey = com.pln.monitoringpln.BuildConfig.SUPABASE_KEY
        ) {
            install(Storage)
            install(io.github.jan.supabase.gotrue.Auth)
        }

        storageRepository = StorageRepositoryImpl(supabaseClient)
        
        // Sign in to bypass RLS
        runBlocking {
            try {
                supabaseClient.auth.signInWith(Email) {
                    email = "teknisi1@pln.co.id"
                    password = "password123"
                }
            } catch (e: Exception) {
                println("Sign in failed: ${e.message}")
                // Continue, maybe session is already active or we can't sign in.
                // If sign in fails, upload might fail too.
            }
        }
    }

    @Test
    fun upload_technician_photo_should_succeed() = runBlocking {
        println(logHeader.format("Integration: Upload Photo"))

        // Given
        val fileName = "test-upload-${System.currentTimeMillis()}.jpg"
        val byteArray = "Test Content".toByteArray()
        println(logAction.format("Upload file: $fileName"))

        // When
        val result = storageRepository.uploadTechnicianPhoto(byteArray, fileName)

        // Then
        if (result.isFailure) {
            println("Error: ${result.exceptionOrNull()?.message}")
        }
        assertTrue("Upload failed: ${result.exceptionOrNull()?.message}", result.isSuccess)
        
        val url = result.getOrNull()
        assertTrue("URL should not be null or empty", !url.isNullOrBlank())
        println(logAssert.format("Upload successful, URL: $url"))

        println(logResult)
    }
}
