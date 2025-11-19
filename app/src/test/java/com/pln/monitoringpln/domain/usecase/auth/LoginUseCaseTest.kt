package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.usecase.auth.LoginUseCase
import com.pln.monitoringpln.domain.usecase.user.FakeUserRepository // Import Fake Shared
import com.pln.monitoringpln.utils.TestObjects // Import Data Dummy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    private lateinit var fakeRepository: FakeUserRepository
    private lateinit var useCase: LoginUseCase

    private val logTestStart = "\n--- 🔴 TEST START: %s ---"
    private val logAct = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logSuccess = "--- ✅ LULUS ---"

    @Before
    fun setUp() {
        // Inisialisasi Fake Repo Shared
        fakeRepository = FakeUserRepository()
        useCase = LoginUseCase(fakeRepository)

        // Setup Data Awal pakai TestObjects
        fakeRepository.addDummyUser(TestObjects.TEKNISI_VALID)
        fakeRepository.addDummyUser(TestObjects.USER_INACTIVE)
    }

    @Test
    fun `login successful, should return Active User`() = runTest {
        println(logTestStart.format("Login Successful"))

        println(logAct.format("Login user '${TestObjects.TEKNISI_VALID.email}'"))
        val result = useCase(TestObjects.TEKNISI_VALID.email, "password123")

        println(logAssert.format("Sukses dan return data user"))
        assertTrue(result.isSuccess)
        assertEquals(TestObjects.TEKNISI_VALID, result.getOrNull())

        println(logSuccess)
    }

    @Test
    fun `login inactive user, should return Failure`() = runTest {
        println(logTestStart.format("Login Inactive User"))

        println(logAct.format("Login user non-aktif '${TestObjects.USER_INACTIVE.email}'"))
        val result = useCase(TestObjects.USER_INACTIVE.email, "password123")

        println(logAssert.format("Gagal karena akun dinonaktifkan"))
        assertTrue(result.isFailure)
        assertEquals("Akun Anda telah dinonaktifkan. Hubungi Admin.", result.exceptionOrNull()?.message)

        println(logSuccess)
    }

    @Test
    fun `login with wrong password, should fail`() = runTest {
        println(logTestStart.format("Wrong Password"))

        println(logAct.format("Login password salah"))
        val result = useCase(TestObjects.TEKNISI_VALID.email, "salah123")

        println(logAssert.format("Gagal kredensial"))
        assertTrue(result.isFailure)
        assertEquals("Kredensial salah", result.exceptionOrNull()?.message)

        println(logSuccess)
    }

    @Test
    fun `login with empty email, should fail validation`() = runTest {
        println(logTestStart.format("Empty Email"))

        val result = useCase("", "password123")

        assertTrue(result.isFailure)
        assertEquals("Email tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logSuccess)
    }

    @Test
    fun `login with invalid email format, should fail validation`() = runTest {
        println(logTestStart.format("Invalid Email Format"))

        val result = useCase("bukan-email", "password123")

        assertTrue(result.isFailure)
        assertEquals("Format email tidak valid.", result.exceptionOrNull()?.message)
        println(logSuccess)
    }
}