package com.pln.monitoringpln.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pln.monitoringpln.data.local.AppDatabase
import com.pln.monitoringpln.utils.TestObjects
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AlatDaoTest {
    // Rule: Hindari lateinit, gunakan private val
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    private val alatDao = db.alatDao()

    // Logging helpers
    private val logHeader = "\n--- 🔴 TEST: %s ---"
    private val logAction = "  [Act] %s..."
    private val logAssert = "  [Assert] %s"
    private val logResult = "--- ✅ LULUS ---\n"

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetActiveAlat() = runBlocking {
        println(logHeader.format("DAO: Insert & Get Active"))

        val alat = TestObjects.createAlat(id = "1", namaAlat = "Trafo 1")
        alatDao.insertAlat(alat)
        println(logAction.format("Inserted Alat 1"))

        val loaded = alatDao.getAlatDetail("1")
        assertNotNull(loaded)
        assertEquals("Trafo 1", loaded?.namaAlat)
        println(logAssert.format("Alat found"))
        println(logResult)
    }

    @Test
    fun archiveAlatShouldHideItFromDetail() = runBlocking {
        println(logHeader.format("DAO: Archive Logic"))

        // Given
        val alat = TestObjects.createAlat(id = "2", namaAlat = "Trafo 2")
        alatDao.insertAlat(alat)

        // When
        alatDao.archiveAlat("2")
        println(logAction.format("Archived Alat 2"))

        // Then
        val loaded = alatDao.getAlatDetail("2")
        assertNull(loaded)
        println(logAssert.format("Alat NOT found via getAlatDetail (Soft Deleted)"))
        println(logResult)
    }

    @Test
    fun getAllActiveAlatShouldExcludeArchived() = runBlocking {
        println(logHeader.format("DAO: Get All Active (Filter Archived)"))

        // Given
        val alatActive = TestObjects.createAlat(id = "3", namaAlat = "Active")
        val alatArchived = TestObjects.createAlat(id = "4", namaAlat = "Archived", isArchived = true)
        
        alatDao.insertAlat(alatActive)
        alatDao.insertAlat(alatArchived)
        println(logAction.format("Inserted 1 Active, 1 Archived"))

        // When
        val list = alatDao.getAllActiveAlat()

        // Then
        assertEquals(1, list.size)
        assertEquals("3", list[0].id)
        println(logAssert.format("Only Active Alat retrieved"))
        println(logResult)
    }

    @Test
    fun insertDuplicateIdShouldReplace() = runBlocking {
        println(logHeader.format("DAO: Insert Duplicate (Replace Strategy)"))

        // Given
        val alat1 = TestObjects.createAlat(id = "5", namaAlat = "Original")
        alatDao.insertAlat(alat1)

        // When
        val alat2 = TestObjects.createAlat(id = "5", namaAlat = "Updated")
        alatDao.insertAlat(alat2)
        println(logAction.format("Inserted Duplicate ID with new name"))

        // Then
        val loaded = alatDao.getAlatDetail("5")
        assertNotNull(loaded)
        assertEquals("Updated", loaded?.namaAlat)
        println(logAssert.format("Alat updated successfully"))
        println(logResult)
    }

    @Test
    fun getNonExistentAlatShouldReturnNull() = runBlocking {
        println(logHeader.format("DAO: Get Non-Existent"))

        val loaded = alatDao.getAlatDetail("999")
        assertNull(loaded)
        println(logAssert.format("Returned null as expected"))
        println(logResult)
    }
}
