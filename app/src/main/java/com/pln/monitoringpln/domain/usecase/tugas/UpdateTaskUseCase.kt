package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import java.util.Date

class UpdateTaskUseCase(
    private val tugasRepository: TugasRepository,
    private val alatRepository: AlatRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        taskId: String,
        judul: String,
        deskripsi: String,
        idAlat: String,
        idTeknisi: String,
        tglJatuhTempo: Date,
        status: String,
    ): Result<Unit> {
        // 1. Validasi Input Dasar
        if (taskId.isBlank()) return Result.failure(IllegalArgumentException("ID Tugas tidak valid."))
        if (judul.isBlank()) return Result.failure(IllegalArgumentException("Judul tidak boleh kosong."))
        if (deskripsi.isBlank()) return Result.failure(IllegalArgumentException("Deskripsi tidak boleh kosong."))

        if (idAlat.isBlank()) return Result.failure(IllegalArgumentException("ID Alat tidak boleh kosong."))
        if (idTeknisi.isBlank()) return Result.failure(IllegalArgumentException("ID Teknisi tidak boleh kosong."))

        // 2. Validasi Tanggal (Opsional: bolehkah update ke tanggal lampau? Asumsi: boleh jika hanya edit typo, tapi warning jika ganti jadwal)
        // Kita gunakan validasi standar: tidak boleh null (sudah dijamin tipe Date)

        // 3. Validasi Keberadaan ALAT
        val alatResult = alatRepository.getAlatDetail(idAlat)
        if (alatResult.isFailure) {
            return Result.failure(Exception("Data Alat tidak ditemukan."))
        }

        // 4. Validasi Keberadaan TEKNISI
        val userResult = userRepository.getTeknisiDetail(idTeknisi)
        if (userResult.isFailure) {
            return Result.failure(Exception("Data Teknisi tidak ditemukan."))
        }

        // 5. Construct Tugas Object
        // Kita perlu mengambil data existing untuk field yang tidak berubah (misal tglDibuat, buktiFoto, dll)
        // Namun, karena kita update full, kita bisa construct baru dengan ID yang sama.
        // Tapi hati-hati dengan field yang tidak di-edit di form (misal tglDibuat).
        // Sebaiknya kita fetch dulu existing task.

        val existingTaskResult = tugasRepository.getTaskDetail(taskId)
        if (existingTaskResult.isFailure) {
            return Result.failure(Exception("Tugas lama tidak ditemukan."))
        }
        val existingTask = existingTaskResult.getOrNull()!!

        val updatedTask = existingTask.copy(
            judul = judul,
            deskripsi = deskripsi,
            idAlat = idAlat,
            idTeknisi = idTeknisi,
            tglJatuhTempo = tglJatuhTempo,
            status = status,
            updatedAt = java.util.Date(),
            // tglDibuat tetap
            // buktiFoto tetap
            // kondisiAkhir tetap
        )

        return tugasRepository.updateTask(updatedTask)
    }
}
