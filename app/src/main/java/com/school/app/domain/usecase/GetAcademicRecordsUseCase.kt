package com.school.app.domain.usecase

import com.school.app.data.repository.AcademicRecordRepository
import com.school.app.domain.model.AcademicRecord

class GetAcademicRecordsUseCase(
    private val academicRecordRepository: AcademicRecordRepository
) {
    suspend operator fun invoke(studentId: String): List<AcademicRecord> {
        return academicRecordRepository.getAcademicRecordsForStudent(studentId)
    }
}