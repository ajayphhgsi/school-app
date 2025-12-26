package com.school.app.domain.usecase

import com.school.app.data.repository.AcademicRecordRepository
import com.school.app.data.repository.StudentRepository
import com.school.app.domain.model.AcademicRecord
import com.school.app.domain.model.Role
import com.school.app.domain.model.User

class AddAcademicRecordUseCase(
    private val academicRecordRepository: AcademicRecordRepository,
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(record: AcademicRecord, currentUser: User): Boolean {
        return when (currentUser.role) {
            Role.TEACHER -> {
                val students = studentRepository.getStudentsByTeacher(currentUser.id)
                val studentIds = students.map { it.id }
                if (record.studentId in studentIds) {
                    academicRecordRepository.addAcademicRecord(record)
                } else {
                    false
                }
            }
            Role.ADMIN -> academicRecordRepository.addAcademicRecord(record)
            else -> false
        }
    }
}