package com.pln.monitoringpln.domain.usecase.user

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import com.pln.monitoringpln.domain.model.User

class AddTeknisiUseCaseTest {

    private lateinit var fakeRepo: com.pln.monitoringpln.data.repository.FakeAuthRepository
    private lateinit var useCase: AddTeknisiUseCase

    private val logTestStart = "\n--- 🔴 TEST START: %s ---"
    private val logAct = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logSuccess = "--- ✅ LULUS ---"

    @Before
    fun setUp() {
        fakeRepo = com.pln.monitoringpln.data.repository.FakeAuthRepository()
        useCase = AddTeknisiUseCase(fakeRepo)
    }

    @Test
    fun `add valid teknisi, should return Success`() = runTest {
        println(logTestStart.format("Add Valid Teknisi"))
        println(logAct.format("Menambah teknisi baru"))

        val result = useCase("Sari Teknisi", "sari@pln.co.id", "password123")

        println(logAssert.format("Sukses"))
        assertTrue(result.isSuccess)
        // Note: useCase returns Result<Unit>, so we just verify success.
        // We can verify repo state if needed.
        assertTrue(fakeRepo.users.contains("sari@pln.co.id"))
        println(logSuccess)
    }

    @Test
    fun `add teknisi with existing email, should return error from repo`() = runTest {
        println(logTestStart.format("Add Teknisi Email Duplikat"))

        println(logAct.format("Menambahkan user pertama..."))
        useCase("Sari Teknisi", "sari@pln.co.id", "password123")

        println(logAct.format("Mencoba daftar lagi dengan email sama"))
        val result = useCase("User Baru", "sari@pln.co.id", "password123")

        println(logAssert.format("Harus gagal"))
        assertTrue(result.isFailure)
        assertEquals("Email sudah terdaftar.", result.exceptionOrNull()?.message)
        println(logSuccess)
    }

    @Test
    fun `add teknisi with empty name, should fail validation`() = runTest {
        println(logTestStart.format("Add Teknisi Nama Kosong"))
        val result = useCase("", "a@b.co.id", "123456")
        assertTrue(result.isFailure)
        assertEquals("Nama tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logSuccess)
    }

    @Test
    fun `add teknisi with invalid email format, should fail validation`() = runTest {
        println(logTestStart.format("Add Teknisi Email Invalid"))
        val result = useCase("Budi", "bukan-email", "123456")
        assertTrue(result.isFailure)
        assertEquals("Email harus pln.co.id atau gmail.com", result.exceptionOrNull()?.message)
        println(logSuccess)
    }

    @Test
    fun `add teknisi with short password, should fail validation`() = runTest {
        println(logTestStart.format("Add Teknisi Password Pendek"))
        val result = useCase("Budi", "budi@pln.co.id", "12345")
        assertTrue(result.isFailure)
        assertEquals("Password minimal 6 karakter.", result.exceptionOrNull()?.message)
        println(logSuccess)
    }
}
