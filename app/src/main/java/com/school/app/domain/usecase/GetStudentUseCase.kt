package com.school.app.domain.usecase

import com.school.app.data.repository.StudentRepository
import com.school.app.domain.model.Student

class GetStudentUseCase(private val studentRepository: StudentRepository) {
    suspend operator fun invoke(id: String): Student? {
        return studentRepository.getStudentById(id)
    }
}