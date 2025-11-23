package com.pln.monitoringpln.utils

import com.pln.monitoringpln.domain.model.Alat
import com.pln.monitoringpln.domain.model.User

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
}
