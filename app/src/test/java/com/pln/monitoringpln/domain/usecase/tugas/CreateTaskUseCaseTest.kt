package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.usecase.alat.FakeAlatRepository
import com.pln.monitoringpln.domain.usecase.user.FakeUserRepository
import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Date

class CreateTaskUseCaseTest {

    private lateinit var fakeTugasRepo: FakeTugasRepository
    private lateinit var fakeAlatRepo: FakeAlatRepository
    private lateinit var fakeUserRepo: FakeUserRepository
    private lateinit var useCase: CreateTaskUseCase

    // Variabel Pesan Log (Agar Rapi)
    private val logTestStart = "\n--- 🔴 TEST START: %s ---"
    private val logAct = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logSuccess = "--- ✅ LULUS ---"

    @Before
    fun setUp() {
        fakeTugasRepo = FakeTugasRepository()
        fakeAlatRepo = FakeAlatRepository()
        fakeUserRepo = FakeUserRepository()

        useCase = CreateTaskUseCase(fakeTugasRepo, fakeAlatRepo, fakeUserRepo)

        // Setup Data Awal Menggunakan Object Helper
        fakeAlatRepo.addDummy(TestObjects.ALAT_VALID)
        fakeUserRepo.addDummy(TestObjects.TEKNISI_VALID)
        fakeUserRepo.addDummy(TestObjects.ADMIN_USER)
    }

    // --- HAPPY PATH ---

    @Test
    fun `create task with valid IDs, should success`() = runTest {
        println(logTestStart.format("Create Valid Task"))

        val besok = Date(System.currentTimeMillis() + 86400000)

        println(logAct.format("Membuat tugas untuk ${TestObjects.ALAT_VALID.namaAlat} oleh ${TestObjects.TEKNISI_VALID.namaLengkap}"))
        val result = useCase(
            judul = "Cek Rutin Bulanan",
            deskripsi = "Cek Rutin Bulanan",
            idAlat = TestObjects.ALAT_VALID.id,
            idTeknisi = TestObjects.TEKNISI_VALID.id,
            tglJatuhTempo = besok,
        )

        println(logAssert.format("Cek sukses dan status default"))
        assertTrue(result.isSuccess)
        assertEquals("To Do", fakeTugasRepo.database[0].status)

        println(logSuccess)
    }

    // --- VALIDATION CASES ---

    @Test
    fun `create task with NON-EXISTENT Alat ID, should fail`() = runTest {
        println(logTestStart.format("Invalid Alat ID"))

        println(logAct.format("Membuat tugas dengan ID Alat 'gaib'"))
        val result = useCase(
            judul = "Cek",
            deskripsi = "Cek",
            idAlat = "id-alat-gaib",
            idTeknisi = TestObjects.TEKNISI_VALID.id,
            tglJatuhTempo = Date(System.currentTimeMillis() + 10000),
        )

        println(logAssert.format("Harus gagal karena alat tidak ditemukan"))
        assertTrue(result.isFailure)
        assertEquals("Data Alat tidak ditemukan.", result.exceptionOrNull()?.message)

        println(logSuccess)
    }

    @Test
    fun `create task with NON-EXISTENT Teknisi ID, should fail`() = runTest {
        println(logTestStart.format("Invalid Teknisi ID"))

        println(logAct.format("Membuat tugas dengan ID Teknisi 'gaib'"))
        val result = useCase(
            judul = "Cek",
            deskripsi = "Cek",
            idAlat = TestObjects.ALAT_VALID.id,
            idTeknisi = "id-tech-gaib",
            tglJatuhTempo = Date(System.currentTimeMillis() + 10000),
        )

        println(logAssert.format("Harus gagal karena teknisi tidak ditemukan"))
        assertTrue(result.isFailure)
        assertEquals("Data Teknisi tidak ditemukan.", result.exceptionOrNull()?.message)

        println(logSuccess)
    }

    @Test
    fun `create task assigned to NON-TEKNISI user, should fail`() = runTest {
        println(logTestStart.format("Assign to Admin (Wrong Role)"))

        println(logAct.format("Assign tugas ke ${TestObjects.ADMIN_USER.namaLengkap} (Role: ${TestObjects.ADMIN_USER.role})"))
        val result = useCase(
            judul = "Cek",
            deskripsi = "Cek",
            idAlat = TestObjects.ALAT_VALID.id,
            idTeknisi = TestObjects.ADMIN_USER.id,
            tglJatuhTempo = Date(System.currentTimeMillis()),
        )

        println(logAssert.format("Harus gagal karena user bukan teknisi"))
        assertTrue(result.isFailure)
        assertEquals("User yang dipilih bukan Teknisi.", result.exceptionOrNull()?.message)

        println(logSuccess)
    }

    @Test
    fun `create task with empty description, should fail`() = runTest {
        println(logTestStart.format("Empty Description"))

        val result = useCase(
            judul = "Judul",
            deskripsi = "",
            idAlat = TestObjects.ALAT_VALID.id,
            idTeknisi = TestObjects.TEKNISI_VALID.id,
            tglJatuhTempo = Date(),
        )

        assertTrue(result.isFailure)
        assertEquals("Deskripsi tidak boleh kosong.", result.exceptionOrNull()?.message)

        println(logSuccess)
    }

    @Test
    fun `create task with past date, should fail`() = runTest {
        println(logTestStart.format("Past Due Date"))

        val kemarin = Date(System.currentTimeMillis() - 86400000)

        val result = useCase(
            judul = "Desc",
            deskripsi = "Desc",
            idAlat = TestObjects.ALAT_VALID.id,
            idTeknisi = TestObjects.TEKNISI_VALID.id,
            tglJatuhTempo = kemarin,
        )

        assertTrue(result.isFailure)
        assertEquals("Tanggal jatuh tempo tidak boleh di masa lalu.", result.exceptionOrNull()?.message)

        println(logSuccess)
    }
}
