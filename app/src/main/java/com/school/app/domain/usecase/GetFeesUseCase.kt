package com.school.app.domain.usecase

import com.school.app.data.repository.FeeRepository
import com.school.app.data.repository.StudentRepository
import com.school.app.domain.model.Fee
import com.school.app.domain.model.Role
import com.school.app.domain.model.User

class GetFeesUseCase(
    private val feeRepository: FeeRepository,
    private val studentRepository: StudentRepository
) {
    suspend operator fun invoke(currentUser: User): List<Fee> {
        return when (currentUser.role) {
            Role.STUDENT -> feeRepository.getFeesForStudent(currentUser.id)
            Role.TEACHER -> {
                val students = studentRepository.getStudentsByTeacher(currentUser.id)
                val studentIds = students.map { it.id }
                feeRepository.getFeesForStudents(studentIds)
            }
            Role.ADMIN -> feeRepository.getAllFees()
        }
    }
}