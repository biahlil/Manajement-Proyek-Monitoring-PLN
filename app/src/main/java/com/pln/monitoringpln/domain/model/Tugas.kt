package com.pln.monitoringpln.domain.model

import java.util.Date

data class Tugas(
    val id: String = "",
    val deskripsi: String,
    val idAlat: String,
    val idTeknisi: String,
    val tglDibuat: Date = Date(),
    val tglJatuhTempo: Date,
    val status: String = "To Do" // Default status
)