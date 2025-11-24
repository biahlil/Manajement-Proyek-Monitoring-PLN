package com.pln.monitoringpln.domain.usecase.report

import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.repository.ReportRepository
import java.util.Date

class FakeReportRepository : ReportRepository {

    // Variabel dikontrol langsung, tidak perlu lateinit
    var shouldFail = false
    var isEmptyData = false

    override suspend fun exportTaskReport(
        startDate: Date,
        endDate: Date,
        format: ExportFormat
    ): Result<String> {
        println("  ➡️ [FakeRepo] Request Export ($format) dari $startDate s/d $endDate")

        if (shouldFail) {
            println("     ❌ Gagal: Simulasi Error IO/Permission")
            return Result.failure(Exception("Gagal membuat file laporan."))
        }

        if (isEmptyData) {
            println("     ❌ Gagal: Data Kosong pada rentang ini")
            return Result.failure(Exception("Tidak ada data untuk diekspor."))
        }

        // Simulasi ekstensi file menggunakan Expression Body 'when' (Clean)
        val extension = when (format) {
            ExportFormat.EXCEL_CSV -> "csv"
            ExportFormat.PDF -> "pdf"
        }

        val dummyPath = "/storage/emulated/0/Download/Laporan_PLN_${startDate.time}.$extension"
        println("     ✅ Sukses: File tersimpan di $dummyPath")
        return Result.success(dummyPath)
    }
}