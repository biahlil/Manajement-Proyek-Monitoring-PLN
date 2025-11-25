package com.pln.monitoringpln.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pln.monitoringpln.data.local.dao.AlatDao
import com.pln.monitoringpln.data.local.entity.AlatEntity

@Database(entities = [AlatEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alatDao(): AlatDao
}
