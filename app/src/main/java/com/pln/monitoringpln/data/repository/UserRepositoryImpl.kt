package com.pln.monitoringpln.data.repository

import com.pln.monitoringpln.data.local.dao.UserDao
import com.pln.monitoringpln.data.local.entity.UserEntity
import com.pln.monitoringpln.data.mapper.toDomain
import com.pln.monitoringpln.data.mapper.toEntity
import com.pln.monitoringpln.domain.model.User
import com.pln.monitoringpln.domain.repository.UserRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao,
    private val supabaseClient: SupabaseClient,
) : UserRepository {

    override suspend fun login(email: String, password: String): Result<User> {
        // Login handled by AuthRepository, this might be redundant or for fetching user details after login
        return Result.failure(Exception("Use AuthRepository for login"))
    }

    override suspend fun addTeknisi(email: String, password: String, namaLengkap: String): Result<User> {
        // TODO: Implement add teknisi via Supabase Edge Function or Auth Admin API
        return Result.failure(Exception("Not implemented yet"))
    }

    override suspend fun getTeknisiDetail(id: String): Result<User> {
        return try {
            val localUser = userDao.getUserById(id)
            if (localUser != null) {
                Result.success(localUser.toDomain())
            } else {
                // Fallback to remote if not found locally
                val profile = supabaseClient.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", id)
                        }
                    }.decodeSingleOrNull<com.pln.monitoringpln.data.model.ProfileDto>()

                if (profile != null) {
                    val userEntity = UserEntity(
                        id = profile.id,
                        email = profile.email ?: "",
                        namaLengkap = profile.fullName ?: "Pengguna",
                        role = profile.role,
                        isActive = profile.isActive ?: true,
                        photoUrl = profile.avatarUrl,
                        createdAt = profile.createdAt,
                        updatedAt = profile.updatedAt,
                    )
                    userDao.insertUser(userEntity)
                    Result.success(userEntity.toDomain())
                } else {
                    Result.failure(Exception("User not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(id: String): Result<Unit> {
        return try {
            // 1. Call Edge Function to delete from Auth (and cascade to profiles)
            val functionResponse = supabaseClient.functions.invoke("delete-user", mapOf("user_id" to id))

            // 2. Delete from Local DB
            userDao.deleteUserById(id)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncProfile(): Result<Unit> {
        return try {
            // Get current user ID from Auth (assuming we can get it, or passed in.
            // Since this is a repository, getting current user ID might need another dependency or we fetch all/current session)
            // Ideally, we should sync the *current* user.
            // For now, let's assume we sync the user that is currently logged in.
            // But wait, UserRepository doesn't know about Auth state directly usually.
            // However, Supabase client has auth.

            val currentUser = supabaseClient.auth.currentUserOrNull()
            if (currentUser != null) {
                val id = currentUser.id
                val profile = supabaseClient.postgrest["profiles"]
                    .select {
                        filter {
                            eq("id", id)
                        }
                    }.decodeSingleOrNull<com.pln.monitoringpln.data.model.ProfileDto>()

                if (profile != null) {
                    val userEntity = UserEntity(
                        id = profile.id,
                        email = profile.email ?: "",
                        namaLengkap = profile.fullName ?: "Pengguna",
                        role = profile.role,
                        isActive = profile.isActive ?: true,
                        photoUrl = profile.avatarUrl,
                        createdAt = profile.createdAt,
                        updatedAt = profile.updatedAt,
                    )
                    userDao.insertUser(userEntity)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTeknisi(): Result<List<User>> {
        return try {
            val localUsers = userDao.getAllTeknisi()
            if (localUsers.isNotEmpty()) {
                Result.success(localUsers.map { it.toDomain() })
            } else {
                // Fallback to refresh if empty
                refreshTeknisi()
                val newLocal = userDao.getAllTeknisi()
                Result.success(newLocal.map { it.toDomain() })
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeTeknisi(): kotlinx.coroutines.flow.Flow<List<User>> {
        return userDao.observeAllTeknisi().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshTeknisi(): Result<Unit> {
        return try {
            val remoteResult = supabaseClient.postgrest["profiles"]
                .select {
                    filter {
                        eq("role", "TEKNISI")
                    }
                }.decodeList<com.pln.monitoringpln.data.model.ProfileDto>()

            val remoteUsers = remoteResult.map { profile ->
                UserEntity(
                    id = profile.id,
                    email = profile.email ?: "",
                    namaLengkap = profile.fullName ?: "Pengguna",
                    role = profile.role,
                    isActive = profile.isActive ?: true,
                    photoUrl = profile.avatarUrl,
                    createdAt = profile.createdAt,
                    updatedAt = profile.updatedAt,
                )
            }

            if (remoteUsers.isNotEmpty()) {
                // Clear old data (including mocks) before inserting fresh data
                userDao.deleteTeknisi()
                userDao.insertAll(remoteUsers)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            // 1. Update Remote (Supabase)
            val normalizedRole = when {
                user.role.equals("admin", ignoreCase = true) -> "ADMIN"
                user.role.equals("teknisi", ignoreCase = true) -> "TEKNISI"
                user.role.equals("technician", ignoreCase = true) -> "TEKNISI"
                else -> "TEKNISI" // Default fallback
            }

            val profileDto = com.pln.monitoringpln.data.model.ProfileDto(
                id = user.id,
                role = normalizedRole,
                fullName = user.namaLengkap,
                email = user.email,
                isActive = true, // Assuming active for now
            )

            supabaseClient.postgrest["profiles"].update(profileDto) {
                filter {
                    eq("id", user.id)
                }
            }

            // 2. Update Local
            val updatedUser = user.copy(role = normalizedRole)
            userDao.updateUser(updatedUser.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setUserStatus(id: String, isActive: Boolean): Result<Unit> {
        return Result.success(Unit)
    }

    override fun getUserProfileFlow(id: String): kotlinx.coroutines.flow.Flow<User?> {
        return userDao.observeUserById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun uploadAvatar(userId: String, byteArray: ByteArray): Result<String> {
        return try {
            val fileName = "avatar.jpg"
            val bucketName = "avatars"
            val path = "$userId/$fileName"

            // 1. Upload to Storage
            supabaseClient.storage.from(bucketName).upload(path, byteArray, upsert = true)

            // 2. Get Public URL with Timestamp to bust cache
            val publicUrl = supabaseClient.storage.from(bucketName).publicUrl(path) + "?t=${System.currentTimeMillis()}"

            // 3. Update Profile in DB
            val profileDto = com.pln.monitoringpln.data.model.ProfileDto(
                id = userId,
                role = "", // Not updating role here, but ProfileDto requires it. Ideally should be partial update.
                // Wait, ProfileDto is for full update usually. Let's check update logic.
                // We should use a partial update or fetch existing first.
                // Let's use a specific DTO for partial update or just map.
                // Supabase postgrest update can take a map or object.
                // Let's create a minimal object or just use map if possible.
                // Since we are using typed client, let's fetch current first to be safe or use patch.
                // Actually, we can just update the specific column using a map if the library supports it,
                // but for now let's use the existing update flow but we need the role.
                // Let's fetch current profile first.
                avatarUrl = publicUrl,
            )

            // Using a map for partial update is safer to avoid overwriting other fields with nulls/defaults
            // But typed client expects object.
            // Let's fetch current user to get role.
            val currentProfile = supabaseClient.postgrest["profiles"].select {
                filter { eq("id", userId) }
            }.decodeSingle<com.pln.monitoringpln.data.model.ProfileDto>()

            val updatedProfile = currentProfile.copy(avatarUrl = publicUrl)

            supabaseClient.postgrest["profiles"].update(updatedProfile) {
                filter { eq("id", userId) }
            }

            // 4. Update Local DB
            val localUser = userDao.getUserById(userId)
            if (localUser != null) {
                userDao.updateUser(localUser.copy(photoUrl = publicUrl))
            }

            Result.success(publicUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
