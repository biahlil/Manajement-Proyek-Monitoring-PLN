package com.pln.monitoringpln.domain.usecase.storage

import com.pln.monitoringpln.data.repository.FakeStorageRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UploadPhotoUseCaseTest {

    private lateinit var fakeRepo: FakeStorageRepository
    private lateinit var useCase: UploadPhotoUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeStorageRepository()
        useCase = UploadPhotoUseCase(fakeRepo)
    }

    @Test
    fun `invoke should return url on success`() = runTest {
        // When
        val result = useCase(ByteArray(0), "photo.jpg")

        // Then
        assertTrue(result.isSuccess)
        assertEquals("https://dummy.url/photo.jpg", result.getOrNull())
    }

    @Test
    fun `invoke should fail when repo fails`() = runTest {
        // Given
        fakeRepo.shouldFail = true

        // When
        val result = useCase(ByteArray(0), "photo.jpg")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Upload failed", result.exceptionOrNull()?.message)
    }
}
