package com.school.app.domain.model

data class User(
    val id: String,
    val email: String,
    val password: String, // Note: In real app, passwords should be hashed
    val role: Role,
    val name: String
)

enum class Role {
    ADMIN, TEACHER, STUDENT
}