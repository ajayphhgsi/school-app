package com.school.app.data.repository

import com.school.app.domain.model.AcademicRecord

interface AcademicRecordRepository {
    suspend fun getAllAcademicRecords(): List<AcademicRecord>
    suspend fun getAcademicRecordsForStudent(studentId: String): List<AcademicRecord>
    suspend fun getAcademicRecordsForStudents(studentIds: List<String>): List<AcademicRecord>
    suspend fun addAcademicRecord(record: AcademicRecord): Boolean
    suspend fun updateAcademicRecord(record: AcademicRecord): Boolean
}