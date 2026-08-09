package com.diabeticmonitor.app.data.repository

import com.diabeticmonitor.app.data.db.dao.UserProfileDao
import com.diabeticmonitor.app.data.db.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val dao: UserProfileDao
) {
    fun getProfile(): Flow<UserProfile?> = dao.getProfile()

    suspend fun getProfileOnce(): UserProfile? = dao.getProfileOnce()

    suspend fun saveProfile(profile: UserProfile) {
        val existing = dao.getProfileOnce()
        if (existing == null) dao.insert(profile) else dao.update(profile)
    }
}
