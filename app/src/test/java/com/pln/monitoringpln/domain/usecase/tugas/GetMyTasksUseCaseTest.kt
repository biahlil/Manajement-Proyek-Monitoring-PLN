package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GetMyTasksUseCaseTest {

    private val fakeRepo = FakeTugasRepository()
    private val useCase = GetMyTasksUseCase(fakeRepo)

    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Before
    fun setUp() {
        fakeRepo.clear()
        // Seeding data: Ada tugas untuk TEKNISI_VALID dan teknisi lain
        fakeRepo.addDummyTasks(listOf(
            TestObjects.TUGAS_TODO,         // Milik TEKNISI_VALID
            TestObjects.TUGAS_IN_PROGRESS,  // Milik TEKNISI_VALID
            TestObjects.TUGAS_OTHER_TECH    // Milik 'tech-2'
        ))
    }

    // --- HAPPY PATH ---

    @Test
    fun `get tasks for TEKNISI_VALID, should return only his tasks`() = runTest {
        println(logHeader.format("Get Tasks for Specific Technician"))

        println(logAction.format("Mengambil tugas untuk '${TestObjects.TEKNISI_VALID.namaLengkap}'"))
        val result = useCase(TestObjects.TEKNISI_VALID.id)

        println(logAssert.format("Sukses dan jumlah tugas harus 2"))
        assertTrue(result.isSuccess)
        val list = result.getOrNull() ?: emptyList()

        // Verifikasi Jumlah
        assertEquals(2, list.size)

        // Verifikasi Kepemilikan (Semua tugas harus milik user ini)
        assertTrue(list.all { it.idTeknisi == TestObjects.TEKNISI_VALID.id })

        // Verifikasi Konten Spesifik
        assertTrue(list.any { it.deskripsi == TestObjects.TUGAS_TODO.deskripsi })
        assertTrue(list.any { it.deskripsi == TestObjects.TUGAS_IN_PROGRESS.deskripsi })

        // Pastikan tugas teknisi lain TIDAK terbawa
        assertFalse(list.any { it.id == TestObjects.TUGAS_OTHER_TECH.id })

        println(logResult)
    }

    @Test
    fun `get tasks for unknown technician, should return empty list`() = runTest {
        println(logHeader.format("Get Tasks for Unknown Technician"))
        val result = useCase("tech-hantu")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
        println(logResult)
    }

    // --- VALIDATION CASES ---

    @Test
    fun `get tasks with empty ID, should fail validation`() = runTest {
        println(logHeader.format("Get Tasks with Empty ID"))
        val result = useCase("")

        assertTrue(result.isFailure)
        assertEquals("ID Teknisi tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    @Test
    fun `get tasks with whitespace ID, should fail validation`() = runTest {
        println(logHeader.format("Get Tasks with Whitespace ID"))
        val result = useCase("   ") // Spasi saja

        assertTrue(result.isFailure)
        assertEquals("ID Teknisi tidak boleh kosong.", result.exceptionOrNull()?.message)
        println(logResult)
    }

    // ==========================================
    // 3. SEARCH & FILTERING CASES (UC8)
    // ==========================================

    @Test
    fun `search tasks with Partial Keyword, should return matching tasks`() = runTest {
        println(logHeader.format("Search: Partial Keyword 'Kabel'"))

        // Act: Cari "Kabel" (Harusnya match "Cek Kabel A")
        val result = useCase(TestObjects.TEKNISI_VALID.id, "Kabel")

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!

        assertEquals(1, list.size)
        assertEquals("Cek Kabel A", list.first().deskripsi)
        println(logResult)
    }

    @Test
    fun `search tasks Case Insensitive, should return matching tasks`() = runTest {
        println(logHeader.format("Search: Case Insensitive 'trafo'"))

        // Act: Cari "trafo" (kecil) -> Harusnya match "Perbaikan Trafo B"
        val result = useCase(TestObjects.TEKNISI_VALID.id, "trafo")

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!

        assertEquals(1, list.size)
        assertEquals("Perbaikan Trafo B", list.first().deskripsi)
        println(logResult)
    }

    @Test
    fun `search tasks Not Found keyword, should return empty list`() = runTest {
        println(logHeader.format("Search: Keyword Not Found"))

        val result = useCase(TestObjects.TEKNISI_VALID.id, "Nasi Goreng")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.isEmpty())
        println(logResult)
    }

    @Test
    fun `search tasks with Empty Query, should return ALL tasks`() = runTest {
        println(logHeader.format("Search: Empty Query"))

        val result = useCase(TestObjects.TEKNISI_VALID.id, "")

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!

        // Harusnya return semua tugas teknisi (2 tugas)
        assertEquals(2, list.size)
        println(logResult)
    }

    @Test
    fun `search tasks with Special Char, should handle gracefully`() = runTest {
        println(logHeader.format("Search: Special Characters"))

        // Setup: Tambah tugas dengan nama aneh
        val weirdTask = TestObjects.TUGAS_TODO.copy(id = "w1", deskripsi = "Cek #@!$")
        fakeRepo.addDummyTasks(listOf(weirdTask))

        // Act
        val result = useCase(TestObjects.TEKNISI_VALID.id, "#@!$")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        println(logResult)
    }

    // ==========================================
    // 3. UNIVERSAL SEARCH CASES (UC8 Enhanced)
    // ==========================================

    @Test
    fun `search by Status 'Progress', should find In Progress tasks`() = runTest {
        println(logHeader.format("Search Universal: By Status"))

        // Act: Cari "Progress"
        val result = useCase(TestObjects.TEKNISI_VALID.id, "Progress")

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!

        assertEquals(1, list.size)
        assertEquals("In Progress", list.first().status)
        println(logResult)
    }

    @Test
    fun `search by Alat ID 'TRF-A', should find tasks related to that Alat`() = runTest {
        println(logHeader.format("Search Universal: By Alat Code/ID"))

        // ID Alat di TestObjects adalah "alat-1", tapi mari kita anggap user cari "alat-1"
        // (Di implementasi nyata, ini akan JOIN ke nama alat, tapi di FakeRepo kita cari ID-nya)
        val result = useCase(TestObjects.TEKNISI_VALID.id, "alat-1")

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!

        // Alat-1 dipakai oleh TUGAS_TODO dan TUGAS_IN_PROGRESS milik teknisi ini
        assertEquals(2, list.size)
        println(logResult)
    }

    @Test
    fun `search by Date (Month Name), should find tasks in that date`() = runTest {
        println(logHeader.format("Search Universal: By Date String"))

        // TestObjects menggunakan Date() (Hari ini).
        // Mari cari format bulan saat ini, misal "Nov" atau "Oct" tergantung waktu run.
        // Agar tes ini selalu lulus, kita ambil format bulan dari data dummy.
        val todayMonth = java.text.SimpleDateFormat("MMM", java.util.Locale.getDefault())
            .format(TestObjects.TUGAS_TODO.tglJatuhTempo)

        println("   [Info] Searching for month: $todayMonth")
        val result = useCase(TestObjects.TEKNISI_VALID.id, todayMonth)

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!

        // Semua tugas di TestObjects dibuat/jatuh tempo bulan ini/besok
        assertTrue(list.isNotEmpty())
        println(logResult)
    }

    @Test
    fun `search by Description 'Kabel', should find matching tasks`() = runTest {
        println(logHeader.format("Search Universal: By Description"))

        val result = useCase(TestObjects.TEKNISI_VALID.id, "Kabel")

        assertTrue(result.isSuccess)
        val list = result.getOrNull()!!

        assertEquals(1, list.size)
        assertEquals("Cek Kabel A", list.first().deskripsi)
        println(logResult)
    }
}