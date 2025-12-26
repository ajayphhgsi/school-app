package com.school.app.domain.usecase

import com.school.app.data.repository.AttendanceRepository
import com.school.app.data.repository.StudentRepository
import com.school.app.domain.model.Attendance
import com.school.app.domain.model.Role
import com.school.app.domain.model.User

class GetAttendancesUseCase(
    private val attendanceRepository: AttendanceRepository,
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(currentUser: User): List<Attendance> {
        return when (currentUser.role) {
            Role.STUDENT -> attendanceRepository.getAttendancesForStudent(currentUser.id)
            Role.TEACHER -> {
                val students = studentRepository.getStudentsByTeacher(currentUser.id)
                val studentIds = students.map { it.id }
                attendanceRepository.getAttendancesForStudents(studentIds)
            }
            Role.ADMIN -> attendanceRepository.getAllAttendances()
        }
    }
}