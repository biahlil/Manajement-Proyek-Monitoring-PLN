package com.pln.monitoringpln.utils

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.Tugas
import com.pln.monitoringpln.domain.model.User
import java.util.Date

object TestObjects {

    // --- USER ---
    val TEKNISI_VALID = User(
        id = "tech-1",
        email = "budi@pln.co.id",
        namaLengkap = "Budi Teknisi",
        role = "Teknisi",
        isActive = true,
    )

    val USER_INACTIVE = User(
        id = "user-inactive",
        email = "mantan@pln.co.id",
        namaLengkap = "Mantan Teknisi",
        role = "Teknisi",
        isActive = false,
    )

    val ADMIN_USER = User(
        id = "admin-1",
        email = "boss@pln.co.id",
        namaLengkap = "Pak Bos",
        role = "Admin",
        isActive = true,
    )

    // --- ALAT (Untuk UC1/UC3 nanti) ---
    val ALAT_VALID = Alat(
        id = "alat-1",
        kodeAlat = "TRF-A",
        namaAlat = "Trafo Gardu A",
        latitude = -6.2,
        longitude = 106.8,
        kondisi = "Baik",
        status = "ACTIVE",
    )

    // --- TUGAS [BARU] ---
    // Menggunakan data dari user & alat yang valid di atas
    val TUGAS_TODO = Tugas(
        id = "task-1",
        deskripsi = "Cek Kabel A",
        idAlat = ALAT_VALID.id,
        idTeknisi = TEKNISI_VALID.id, // tech-1
        tglJatuhTempo = Date(System.currentTimeMillis() + 86400000), // Besok
        status = "TODO",
    )

    val TUGAS_IN_PROGRESS = Tugas(
        id = "task-2",
        deskripsi = "Perbaikan Trafo B",
        idAlat = ALAT_VALID.id,
        idTeknisi = TEKNISI_VALID.id, // tech-1
        tglJatuhTempo = Date(System.currentTimeMillis() + 86400000),
        status = "IN_PROGRESS",
    )

    val TUGAS_OTHER_TECH = Tugas(
        id = "task-3",
        deskripsi = "Inspeksi Rutin",
        idAlat = ALAT_VALID.id,
        idTeknisi = "tech-2", // Teknisi Beda
        tglJatuhTempo = Date(System.currentTimeMillis() + 86400000),
        status = "TODO",
    )
    fun createAlat(
        id: String = "alat-1",
        kodeAlat: String = "TRF-A",
        namaAlat: String = "Trafo Gardu A",
        latitude: Double = -6.2,
        longitude: Double = 106.8,
        kondisi: String = "Baik",
        status: String = "ACTIVE",
        isArchived: Boolean = false,
    ): Alat {
        return Alat(
            id = id,
            kodeAlat = kodeAlat,
            namaAlat = namaAlat,
            latitude = latitude,
            longitude = longitude,
            kondisi = kondisi,
            status = status,
            isArchived = isArchived,
        )
    }
}
