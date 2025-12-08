package com.pln.monitoringpln.domain.usecase.user

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UploadAvatarUseCaseTest {

    private lateinit var fakeRepo: FakeUserRepository
    private lateinit var useCase: UploadAvatarUseCase

    @Before
    fun setUp() {
        fakeRepo = FakeUserRepository()
        useCase = UploadAvatarUseCase(fakeRepo)
    }

    @Test
    fun `invoke should return url on success`() = runTest {
        // When
        val result = useCase("user-1", ByteArray(0))

        // Then
        assertTrue(result.isSuccess)
        assertEquals("https://dummy-avatar.com/user-1.jpg", result.getOrNull())
    }
}
