package com.pln.monitoringpln.data.repository

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.pln.monitoringpln.data.mapper.toDomain
import com.pln.monitoringpln.domain.model.ExportFormat
import com.pln.monitoringpln.domain.repository.ReportRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.pln.monitoringpln.data.local.dao.TugasDao
import com.pln.monitoringpln.data.local.dao.UserDao
import com.pln.monitoringpln.data.local.dao.AlatDao
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.model.Alat

class ReportRepositoryImpl(
    private val tugasDao: TugasDao,
    private val userDao: UserDao,
    private val alatDao: AlatDao,
    private val context: Context,
) : ReportRepository {

    override suspend fun exportTaskReport(
        startDate: Date,
        endDate: Date,
        format: ExportFormat,
    ): Result<String> {
        return try {
            val tasks = tugasDao.getTasksByDateRange(startDate.time, endDate.time)
                .map { it.toDomain() }

            if (tasks.isEmpty()) {
                return Result.failure(Exception("Tidak ada data tugas pada rentang tanggal tersebut."))
            }

            val fileName = "Laporan_Tugas_${System.currentTimeMillis()}"
            val filePath = when (format) {
                ExportFormat.PDF -> generatePdf(fileName, tasks, startDate, endDate)
                ExportFormat.EXCEL_CSV -> generateCsv(fileName, tasks)
            }

            Result.success(filePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun exportFullDatabaseReport(format: ExportFormat): Result<String> {
        return try {
            val tasks = tugasDao.getAllTasksSync().map { it.toDomain() }
            val technicians = userDao.getAllTeknisi().map { it.toDomain() }
            val equipment = alatDao.getAllActiveAlat().map { it.toDomain() }

            val fileName = "Full_Laporan_Database_${System.currentTimeMillis()}"
            val filePath = when (format) {
                ExportFormat.PDF -> generateFullPdf(fileName, tasks, technicians, equipment)
                ExportFormat.EXCEL_CSV -> generateFullCsv(fileName, tasks, technicians, equipment)
            }
            Result.success(filePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun saveToDownloads(fileName: String, mimeType: String, writeBlock: (OutputStream) -> Unit): String = withContext(Dispatchers.IO) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: throw Exception("Failed to create file in Downloads")

            resolver.openOutputStream(uri)?.use { outputStream ->
                writeBlock(outputStream)
            } ?: throw Exception("Failed to open output stream")

            // Return a user-friendly path or URI string
            // For user display, we can say "Downloads/fileName"
            "${Environment.DIRECTORY_DOWNLOADS}/$fileName"
        } else {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir.exists()) downloadsDir.mkdirs()
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use { outputStream ->
                writeBlock(outputStream)
            }
            file.absolutePath
        }
    }

    private suspend fun generatePdf(fileName: String, tasks: List<com.pln.monitoringpln.domain.model.Tugas>, startDate: Date, endDate: Date): String {
        return saveToDownloads("$fileName.pdf", "application/pdf") { outputStream ->
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)

            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

            document.add(Paragraph("Laporan Monitoring Tugas PLN").setBold().setFontSize(18f))
            document.add(Paragraph("Periode: ${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}"))
            document.add(Paragraph("\n"))

            addTasksTable(document, tasks, dateFormat)

            document.close()
        }
    }

    private suspend fun generateFullPdf(
        fileName: String,
        tasks: List<Tugas>,
        technicians: List<User>,
        equipment: List<Alat>,
    ): String {
        return saveToDownloads("$fileName.pdf", "application/pdf") { outputStream ->
            val writer = PdfWriter(outputStream)
            val pdf = PdfDocument(writer)
            val document = Document(pdf)
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))

            document.add(Paragraph("Laporan Full Database PLN").setBold().setFontSize(18f))
            document.add(Paragraph("Tanggal Generate: ${dateFormat.format(Date())}"))
            document.add(Paragraph("\n"))

            document.add(Paragraph("Daftar Tugas").setBold().setFontSize(14f))
            addTasksTable(document, tasks, dateFormat)
            document.add(Paragraph("\n"))

            document.add(Paragraph("Daftar Teknisi").setBold().setFontSize(14f))
            addTechniciansTable(document, technicians)
            document.add(Paragraph("\n"))

            document.add(Paragraph("Daftar Alat").setBold().setFontSize(14f))
            addEquipmentTable(document, equipment)

            document.close()
        }
    }

    private fun addTasksTable(document: Document, tasks: List<com.pln.monitoringpln.domain.model.Tugas>, dateFormat: SimpleDateFormat) {
        val table = Table(floatArrayOf(1f, 3f, 2f, 2f, 2f))
        table.addHeaderCell("No")
        table.addHeaderCell("Deskripsi")
        table.addHeaderCell("Status")
        table.addHeaderCell("Jatuh Tempo")
        table.addHeaderCell("Teknisi")

        tasks.forEachIndexed { index, tugas ->
            table.addCell(Paragraph((index + 1).toString()))
            table.addCell(Paragraph(tugas.deskripsi))
            table.addCell(Paragraph(tugas.status))
            table.addCell(Paragraph(dateFormat.format(tugas.tglJatuhTempo)))
            table.addCell(Paragraph(tugas.idTeknisi))
        }
        document.add(table)
    }

    private fun addTechniciansTable(document: Document, technicians: List<com.pln.monitoringpln.domain.model.User>) {
        val table = Table(floatArrayOf(1f, 3f, 3f, 2f))
        table.addHeaderCell("No")
        table.addHeaderCell("Nama Lengkap")
        table.addHeaderCell("Email")
        table.addHeaderCell("Role")

        technicians.forEachIndexed { index, user ->
            table.addCell(Paragraph((index + 1).toString()))
            table.addCell(Paragraph(user.namaLengkap))
            table.addCell(Paragraph(user.email))
            table.addCell(Paragraph(user.role))
        }
        document.add(table)
    }

    private fun addEquipmentTable(document: Document, equipment: List<com.pln.monitoringpln.domain.model.Alat>) {
        val table = Table(floatArrayOf(1f, 3f, 2f, 2f, 2f))
        table.addHeaderCell("No")
        table.addHeaderCell("Nama Alat")
        table.addHeaderCell("Jenis")
        table.addHeaderCell("Lokasi")
        table.addHeaderCell("Kondisi")

        equipment.forEachIndexed { index, alat ->
            table.addCell(Paragraph((index + 1).toString()))
            table.addCell(Paragraph(alat.namaAlat))
            table.addCell(Paragraph(alat.tipe))
            table.addCell(Paragraph("${alat.latitude}, ${alat.longitude}"))
            table.addCell(Paragraph(alat.kondisi))
        }
        document.add(table)
    }

    private suspend fun generateCsv(fileName: String, tasks: List<com.pln.monitoringpln.domain.model.Tugas>): String {
        return saveToDownloads("$fileName.csv", "text/csv") { outputStream ->
            val writer = outputStream.bufferedWriter()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            writer.write("No,Deskripsi,Status,Jatuh Tempo,ID Teknisi\n")
            tasks.forEachIndexed { index, tugas ->
                val line = "${index + 1},\"${tugas.deskripsi}\",${tugas.status},${dateFormat.format(tugas.tglJatuhTempo)},${tugas.idTeknisi}\n"
                writer.write(line)
            }
            writer.flush()
            // Don't close writer here as it closes the underlying stream which might be needed if we were doing more,
            // but here it's fine. However, use block handles closing.
            // Actually bufferedWriter.close() closes the underlying stream.
            // If we close it here, the 'use' block in saveToDownloads might try to close it again.
            // It's safer to just flush.
        }
    }

    private suspend fun generateFullCsv(
        fileName: String,
        tasks: List<com.pln.monitoringpln.domain.model.Tugas>,
        technicians: List<com.pln.monitoringpln.domain.model.User>,
        equipment: List<com.pln.monitoringpln.domain.model.Alat>,
    ): String {
        return saveToDownloads("$fileName.csv", "text/csv") { outputStream ->
            val writer = outputStream.bufferedWriter()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

            // 1. Tasks Section
            writer.write("DAFTAR TUGAS\n")
            writer.write("No,Deskripsi,Status,Jatuh Tempo,ID Teknisi\n")
            tasks.forEachIndexed { index, tugas ->
                writer.write("${index + 1},\"${tugas.deskripsi}\",${tugas.status},${dateFormat.format(tugas.tglJatuhTempo)},${tugas.idTeknisi}\n")
            }
            writer.write("\n") // Empty line separator

            // 2. Technicians Section
            writer.write("DAFTAR TEKNISI\n")
            writer.write("No,Nama Lengkap,Email,Role\n")
            technicians.forEachIndexed { index, user ->
                writer.write("${index + 1},\"${user.namaLengkap}\",${user.email},${user.role}\n")
            }
            writer.write("\n") // Empty line separator

            // 3. Equipment Section
            writer.write("DAFTAR ALAT\n")
            writer.write("No,Nama Alat,Jenis,Lokasi,Kondisi\n")
            equipment.forEachIndexed { index, alat ->
                writer.write("${index + 1},\"${alat.namaAlat}\",\"${alat.tipe}\",\"${alat.latitude}, ${alat.longitude}\",${alat.kondisi}\n")
            }

            writer.flush()
        }
    }
}
