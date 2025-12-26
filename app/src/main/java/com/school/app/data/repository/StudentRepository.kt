package com.school.app.data.repository

import com.school.app.domain.model.Student

interface StudentRepository {
    suspend fun getStudentById(id: String): Student?
    suspend fun getAllStudents(): List<Student>
    suspend fun getStudentsByTeacher(teacherId: String): List<Student> // For teachers to see their students
}