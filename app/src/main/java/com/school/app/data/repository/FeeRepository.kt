package com.school.app.data.repository

import com.school.app.domain.model.Fee

interface FeeRepository {
    suspend fun getAllFees(): List<Fee>
    suspend fun getFeesForStudent(studentId: String): List<Fee>
    suspend fun getFeesForStudents(studentIds: List<String>): List<Fee>
}