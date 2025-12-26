package com.school.app.data.repository

import com.school.app.domain.model.User

interface UserRepository {
    suspend fun authenticate(email: String, password: String): User?
}