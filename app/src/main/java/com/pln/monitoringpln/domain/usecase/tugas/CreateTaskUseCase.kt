package com.pln.monitoringpln.domain.usecase.tugas

import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.repository.AlatRepository
import com.pln.monitoringpln.domain.repository.TugasRepository
import com.pln.monitoringpln.domain.repository.UserRepository
import java.util.Calendar
import java.util.Date

class CreateTaskUseCase(
    private val tugasRepository: TugasRepository,
    private val alatRepository: AlatRepository, // Inject Repo Alat
    private val userRepository: UserRepository, // Inject Repo User
) {

    suspend operator fun invoke(
        deskripsi: String,
        idAlat: String,
        idTeknisi: String,
        tglJatuhTempo: Date,
    ): Result<Unit> {
        // 1. Validasi Input Dasar
        if (deskripsi.isBlank()) return Result.failure(IllegalArgumentException("Deskripsi tidak boleh kosong."))
        if (idAlat.isBlank()) return Result.failure(IllegalArgumentException("ID Alat tidak boleh kosong."))
        if (idTeknisi.isBlank()) return Result.failure(IllegalArgumentException("ID Teknisi tidak boleh kosong."))

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val todayMidnight = calendar.time

        // 2. Cek apakah tglJatuhTempo SEBELUM hari ini (jam 00:00)
        // Jika tglJatuhTempo adalah "Hari Ini 00:00", before() akan return false (Lolos).
        // Jika tglJatuhTempo adalah "Kemarin", before() akan return true (Gagal).
        if (tglJatuhTempo.before(todayMidnight)) {
            return Result.failure(IllegalArgumentException("Tanggal jatuh tempo tidak boleh di masa lalu."))
        }

        // 2. Validasi Keberadaan ALAT
        val alatResult = alatRepository.getAlatDetail(idAlat)
        if (alatResult.isFailure) {
            return Result.failure(Exception("Data Alat tidak ditemukan."))
        }

        // 3. Validasi Keberadaan TEKNISI
        val userResult = userRepository.getTeknisiDetail(idTeknisi)
        if (userResult.isFailure) {
            return Result.failure(Exception("Data Teknisi tidak ditemukan."))
        }

        // Cek Role (Harus Teknisi)
        if (userResult.getOrNull()?.role != "Teknisi") {
            return Result.failure(Exception("User yang dipilih bukan Teknisi."))
        }

        // 4. Buat Tugas
        val tugas = Tugas(
            deskripsi = deskripsi,
            idAlat = idAlat,
            idTeknisi = idTeknisi,
            tglJatuhTempo = tglJatuhTempo,
            status = "To Do",
        )

        val createdTask = tugasRepository.createTask(tugas)

        // Cek apakah berhasil dibuat (tidak null)
        return if (createdTask != null) {
            // Jika ada datanya, return Sukses (Unit)
            Result.success(Unit)
        } else {
            // Jika null, return Gagal
            Result.failure(Exception("Gagal membuat tugas. Terjadi kesalahan database."))
        }
    }
}
