package com.pln.monitoringpln.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pln.monitoringpln.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @androidx.room.Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE UPPER(role) = 'TEKNISI' AND isActive = 1 ORDER BY updatedAt DESC")
    suspend fun getAllTeknisi(): List<UserEntity>

    @Query("SELECT * FROM users WHERE UPPER(role) = 'TEKNISI' AND isActive = 1 ORDER BY updatedAt DESC")
    fun observeAllTeknisi(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeUserById(id: String): Flow<UserEntity?>

    @Query("DELETE FROM users WHERE UPPER(role) = 'TEKNISI'")
    suspend fun deleteTeknisi()

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}
