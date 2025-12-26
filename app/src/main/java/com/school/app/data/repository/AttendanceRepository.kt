package com.school.app.data.repository

import com.school.app.domain.model.Attendance

interface AttendanceRepository {
    suspend fun getAllAttendances(): List<Attendance>
    suspend fun getAttendancesForStudent(studentId: String): List<Attendance>
    suspend fun getAttendancesForStudents(studentIds: List<String>): List<Attendance>
    suspend fun markAttendance(attendance: Attendance): Boolean
}