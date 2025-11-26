package com.pln.monitoringpln.data.repository

import android.content.Context
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.pln.monitoringpln.data.model.TugasDto
import com.pln.monitoringpln.data.model.toDomain
import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.repository.ReportRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportRepositoryImpl(
    private val supabaseClient: SupabaseClient,
    private val context: Context
) : ReportRepository {

    override suspend fun exportTaskReport(
        startDate: Date,
        endDate: Date,
        format: ExportFormat
    ): Result<String> {
        return try {
            // 1. Fetch Data
            val tasks = supabaseClient.postgrest["tugas"]
                .select {
                    filter {
                        gte("due_date", startDate)
                        lte("due_date", endDate)
                    }
                }.decodeList<TugasDto>().map { it.toDomain() }

            if (tasks.isEmpty()) {
                return Result.failure(Exception("Tidak ada data tugas pada rentang tanggal tersebut."))
            }

            // 2. Generate File
            val fileName = "Laporan_Tugas_${System.currentTimeMillis()}"
            val file = when (format) {
                ExportFormat.PDF -> generatePdf(fileName, tasks, startDate, endDate)
                ExportFormat.EXCEL_CSV -> generateCsv(fileName, tasks)
            }

            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generatePdf(fileName: String, tasks: List<com.pln.monitoringpln.domain.model.Tugas>, startDate: Date, endDate: Date): File = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(null), "$fileName.pdf")
        val writer = PdfWriter(file)
        val pdf = PdfDocument(writer)
        val document = Document(pdf)

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

        document.add(Paragraph("Laporan Monitoring Tugas PLN").setBold().setFontSize(18f))
        document.add(Paragraph("Periode: ${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"))
        document.add(Paragraph("\n"))

        val table = Table(floatArrayOf(1f, 3f, 2f, 2f, 2f))
        table.addHeaderCell("No")
        table.addHeaderCell("Deskripsi")
        table.addHeaderCell("Status")
        table.addHeaderCell("Jatuh Tempo")
        table.addHeaderCell("Teknisi")

        tasks.forEachIndexed { index, tugas ->
            table.addCell((index + 1).toString())
            table.addCell(tugas.deskripsi)
            table.addCell(tugas.status)
            table.addCell(dateFormat.format(tugas.tglJatuhTempo))
            table.addCell(tugas.idTeknisi) // Idealnya nama teknisi, tapi perlu fetch user details
        }

        document.add(table)
        document.close()
        file
    }

    private suspend fun generateCsv(fileName: String, tasks: List<com.pln.monitoringpln.domain.model.Tugas>): File = withContext(Dispatchers.IO) {
        val file = File(context.getExternalFilesDir(null), "$fileName.csv")
        val writer = FileOutputStream(file).bufferedWriter()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        writer.write("No,Deskripsi,Status,Jatuh Tempo,ID Teknisi\n")
        tasks.forEachIndexed { index, tugas ->
            val line = "${index + 1},\"${tugas.deskripsi}\",${tugas.status},${dateFormat.format(tugas.tglJatuhTempo)},${tugas.idTeknisi}\n"
            writer.write(line)
        }
        writer.close()
        file
    }
}
