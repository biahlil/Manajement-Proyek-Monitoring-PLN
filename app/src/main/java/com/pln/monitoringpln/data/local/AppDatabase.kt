package com.pln.monitoringpln.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.pln.monitoringpln.data.local.dao.AlatDao
import com.pln.monitoringpln.data.local.dao.TugasDao
import com.pln.monitoringpln.data.local.entity.AlatEntity
import com.pln.monitoringpln.data.local.entity.TugasEntity

@Database(entities = [AlatEntity::class, TugasEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alatDao(): AlatDao
    abstract fun tugasDao(): TugasDao
}
