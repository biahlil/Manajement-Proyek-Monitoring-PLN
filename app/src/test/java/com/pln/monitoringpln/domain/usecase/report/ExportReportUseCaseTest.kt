package com.pln.monitoringpln.domain.usecase.report

import com.pln.monitoringpln.domain.model.ExportFormat
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.util.Date

class ExportReportUseCaseTest {

    // REKOMENDASI DEEPSOURCE:
    // Hindari lateinit. Inisialisasi langsung agar Null Safety terjamin.
    private val fakeRepo = FakeReportRepository()
    private val useCase = ExportReportUseCase(fakeRepo)

    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @Test
    fun `export as EXCEL_CSV, should return file with csv extension`() = runTest {
        println(logHeader.format("Export Format: EXCEL (CSV)"))

        // Act
        val result = useCase(Date(), Date(), ExportFormat.EXCEL_CSV)

        // Assert
        assertTrue(result.isSuccess)
        val path = result.getOrNull() ?: ""
        println(logAssert.format("Path harus berakhiran .csv"))
        assertTrue("File harus berekstensi .csv", path.endsWith(".csv"))

        println(logResult)
    }

    @Test
    fun `export as PDF, should return file with pdf extension`() = runTest {
        println(logHeader.format("Export Format: PDF"))

        // Act
        val result = useCase(Date(), Date(), ExportFormat.PDF)

        // Assert
        assertTrue(result.isSuccess)
        val path = result.getOrNull() ?: ""
        println(logAssert.format("Path harus berakhiran .pdf"))
        assertTrue("File harus berekstensi .pdf", path.endsWith(".pdf"))

        println(logResult)
    }

    @Test
    fun `export with valid date range, should return file path`() = runTest {
        println(logHeader.format("Happy Path: Valid Range"))

        val start = Date(System.currentTimeMillis() - 86400000) // Kemarin
        val end = Date() // Hari ini

        println(logAction.format("Export data kemarin s/d hari ini"))
        val result = useCase(start, end, ExportFormat.PDF)

        println(logAssert.format("Sukses & Return Path"))
        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()!!.contains("/storage/emulated/0/Download/"))

        println(logResult)
    }

    @Test
    fun `export with Start Date AFTER End Date, should fail validation`() = runTest {
        println(logHeader.format("Validation: Invalid Date Range"))

        val start = Date(System.currentTimeMillis() + 86400000) // Besok
        val end = Date() // Hari ini

        println(logAction.format("Export (Start > End)"))
        val result = useCase(start, end, ExportFormat.PDF)

        println(logAssert.format("Gagal Validasi"))
        assertTrue(result.isFailure)
        assertEquals("Tanggal mulai tidak boleh setelah tanggal akhir.", result.exceptionOrNull()?.message)

        println(logResult)
    }

    @Test
    fun `export when NO DATA available, should return specific error`() = runTest {
        println(logHeader.format("Negative Case: No Data"))

        // Arrange
        fakeRepo.isEmptyData = true // Simulasi flag

        // Act
        val result = useCase(Date(), Date(), ExportFormat.PDF)

        // Assert
        println(logAssert.format("Gagal: Tidak ada data"))
        assertTrue(result.isFailure)
        assertEquals("Tidak ada data untuk diekspor.", result.exceptionOrNull()?.message)

        println(logResult)
    }

    @Test
    fun `export when Repo Fails (IO Error), should fail gracefully`() = runTest {
        println(logHeader.format("Negative Case: IO Error"))

        // Arrange
        fakeRepo.shouldFail = true

        // Act
        val result = useCase(Date(), Date(), ExportFormat.PDF)

        // Assert
        println(logAssert.format("Gagal: Error System"))
        assertTrue(result.isFailure)
        assertEquals("Gagal membuat file laporan.", result.exceptionOrNull()?.message)

        println(logResult)
    }
}
