package com.school.app.domain.usecase

import com.school.app.data.repository.AttendanceRepository
import com.school.app.data.repository.StudentRepository
import com.school.app.domain.model.Attendance
import com.school.app.domain.model.Role
import com.school.app.domain.model.User

class MarkAttendanceUseCase(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(attendance: Attendance, currentUser: User): Boolean {
        return when (currentUser.role) {
            Role.TEACHER -> {
                val students = studentRepository.getStudentsByTeacher(currentUser.id)
                val studentIds = students.map { it.id }
                if (attendance.studentId in studentIds) {
                    attendanceRepository.markAttendance(attendance)
                } else {
                    false
                }
            }
            Role.ADMIN -> attendanceRepository.markAttendance(attendance)
            else -> false
        }
    }
}