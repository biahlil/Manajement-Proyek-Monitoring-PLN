package com.pln.monitoringpln.domain.usecase.auth

import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginUseCaseTest {

    // 1. Buat Fake Repository-nya dulu
    // Ini adalah "kembaran" palsu dari UserRepository
    private lateinit var fakeRepository: FakeUserRepository

    // 2. Ini adalah unit yang akan kita tes
    private lateinit var useCase: LoginUseCase

    // Definisikan Fake Repository di dalam file tes
    class FakeUserRepository : UserRepository {
        // Data user palsu yang akan kita kembalikan
        private val fakeUser = User(
            id = "user-123",
            email = "test@example.com",
            namaLengkap = "Test User",
            role = "Teknisi"
        )

        override suspend fun login(email: String, password: String): Result<User> {
            println("  ➡️ FAKE REPO: login() dipanggil dengan email: $email")

            // Simulasikan: jika email-nya adalah email fakeUser, login sukses
            if (email == fakeUser.email) {
                return Result.success(fakeUser)
            }
            // Simulasikan: jika gagal (misal password salah)
            return Result.failure(Exception("Kredensial salah"))
        }
    }

    @Before
    fun setUp() {
        println("--- 🟢 SETUP: Inisialisasi FakeUserRepository dan LoginUseCase ---")
        fakeRepository = FakeUserRepository()
        useCase = LoginUseCase(fakeRepository)
    }
    // 3. Tes pertama kita (Happy Path)
    @Test
    fun `login successful, should call repository`() = runTest {
        println("\n--- 🔴 TEST: `login successful, should call repository` ---")

        // Arrange (Persiapan)
        val email = "test@example.com"
        val password = "password123"
        // Ini adalah user yang kita harapkan kembali dari FakeRepository
        val expectedUser = User(
            id = "user-123",
            email = "test@example.com",
            namaLengkap = "Test User",
            role = "Teknisi"
        )

        // Act
        println("  [Act]: Memanggil use case...")
        val result = useCase(email, password) // UseCase sekarang mengembalikan hasil

        // Assert
        println("  [Assert]: Memeriksa apakah hasilnya Sukses dan data User-nya benar")
        assertTrue("Hasil login seharusnya Sukses", result.isSuccess)
        assertEquals(expectedUser, result.getOrNull()) // <-- ASSERTION UTAMA

        println("--- ✅ LULUS ---")
    }

    @Test
    fun `login with empty email, should return Failure with correct message`() = runTest {
        println("\n--- 🔴 TEST: `login with empty email` ---")

        // Act
        println("  [Act]: Memanggil use case dengan email kosong...")
        val result = useCase(email = "", password = "password123") // Panggil langsung

        // Assert
        println("  [Assert]: Memeriksa hasilnya Failure dan pesannya benar")
        assertTrue("Hasilnya seharusnya Gagal (isFailure)", result.isFailure)

        // Ambil exception dari Result
        val exception = result.exceptionOrNull()
        assertTrue("Exception seharusnya IllegalArgumentException", exception is IllegalArgumentException)

        // Ini adalah assertion yang Anda minta:
        assertEquals("Email tidak boleh kosong.", exception?.message)

        println("--- ✅ LULUS (Gagal dengan pesan yang benar) ---")
    }

    @Test
    fun `login with invalid email format, should return Failure with correct message`() = runTest {
        println("\n--- 🔴 TEST: `login with invalid email format` ---")

        // Act
        println("  [Act]: Memanggil use case dengan format email salah...")
        val result = useCase(email = "ini-bukan-email", password = "password123")

        // Assert
        println("  [Assert]: Memeriksa hasilnya Failure dan pesannya benar")
        assertTrue("Hasilnya seharusnya Gagal (isFailure)", result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue("Exception seharusnya IllegalArgumentException", exception is IllegalArgumentException)

        // Ini adalah assertion yang Anda minta:
        assertEquals("Format email tidak valid.", exception?.message)

        println("--- ✅ LULUS (Gagal dengan pesan yang benar) ---")
    }

    @Test
    fun `login with empty password, should return Failure with correct message`() = runTest {
        println("\n--- 🔴 TEST: `login with empty password` ---")

        // Act
        println("  [Act]: Memanggil use case dengan password kosong...")
        val result = useCase(email = "test@example.com", password = "")

        // Assert
        println("  [Assert]: Memeriksa hasilnya Failure dan pesannya benar")
        assertTrue("Hasilnya seharusnya Gagal (isFailure)", result.isFailure)

        val exception = result.exceptionOrNull()
        assertTrue("Exception seharusnya IllegalArgumentException", exception is IllegalArgumentException)

        // Ini adalah assertion yang Anda minta:
        assertEquals("Password tidak boleh kosong.", exception?.message)

        println("--- ✅ LULUS (Gagal dengan pesan yang benar) ---")
    }

    @Test
    fun `login with valid format but wrong email, should return Failure`() = runTest {
        println("\n--- 🔴 TEST: `login with valid format but wrong email` ---")

        // Arrange
        println("  [Arrange]: Menyiapkan email VALID format, tapi SALAH kredensial")
        // Email "wrong@example.com" akan LULUS validasi format,
        // tapi akan GAGAL di logika 'if' FakeRepository
        val email = "wrong@example.com"
        val password = "password123"

        // Act
        println("  [Act]: Memanggil use case...")
        val result = useCase(email, password) // UseCase akan validasi, lalu panggil repo

        // Assert
        println("  [Assert]: Memeriksa apakah hasilnya adalah Failure")
        assertTrue("Hasil login seharusnya Gagal", result.isFailure)

        // Memeriksa apakah pesan error dari repository diteruskan dengan benar
        assertEquals("Kredensial salah", result.exceptionOrNull()?.message)

        println("--- ✅ LULUS (Gagal seperti yang diharapkan) ---")
    }
}

