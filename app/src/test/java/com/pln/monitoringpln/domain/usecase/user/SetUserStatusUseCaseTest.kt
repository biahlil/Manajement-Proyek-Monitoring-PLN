package com.pln.monitoringpln.domain.usecase.user

import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SetUserStatusUseCaseTest {

    private lateinit var fakeRepo: FakeUserRepository
    private lateinit var useCase: SetUserStatusUseCase

    private val logTestStart = "\n--- 🔴 TEST START: %s ---"
    private val logAct = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logSuccess = "--- ✅ LULUS ---"

    @Before
    fun setUp() {
        fakeRepo = FakeUserRepository()
        useCase = SetUserStatusUseCase(fakeRepo)
        fakeRepo.addDummy(TestObjects.TEKNISI_VALID)
    }

    @Test
    fun `deactivate user, status should change to false`() = runTest {
        println(logTestStart.format("Deactivate User"))
        println(logAct.format("Menonaktifkan user"))

        val result = useCase(TestObjects.TEKNISI_VALID.id, false)

        println(logAssert.format("Sukses & isActive == false"))
        assertTrue(result.isSuccess)
        val updatedUser = fakeRepo.users[TestObjects.TEKNISI_VALID.id]
        assertEquals(false, updatedUser?.isActive)
        println(logSuccess)
    }
}
