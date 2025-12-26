package com.school.app.domain.usecase

import com.school.app.data.repository.StudentRepository
import com.school.app.domain.model.Student

class GetStudentsUseCase(private val studentRepository: StudentRepository) {
    suspend operator fun invoke(): List<Student> {
        return studentRepository.getAllStudents()
    }
}