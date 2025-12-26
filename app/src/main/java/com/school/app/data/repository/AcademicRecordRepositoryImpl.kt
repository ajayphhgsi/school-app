package com.school.app.data.repository

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.school.app.domain.model.AcademicRecord
import com.school.app.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AcademicRecordRepositoryImpl(private val context: Context) : AcademicRecordRepository {

    private suspend fun getSheetsService(): Sheets = withContext(Dispatchers.IO) {
        val credential = GoogleCredential.fromStream(context.assets.open("service_account.json"))
            .createScoped(listOf(SheetsScopes.SPREADSHEETS))
        Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("School App")
            .build()
    }

    override suspend fun getAllAcademicRecords(): List<AcademicRecord> = withContext(Dispatchers.IO) {
        val records = mutableListOf<AcademicRecord>()
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.ACADEMIC_RECORDS_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values.drop(1)) { // Assuming first row is header
                    if (row.size >= 7) {
                        val id = row[0].toString()
                        val studentId = row[1].toString()
                        val subject = row[2].toString()
                        val grade = row[3].toString()
                        val semester = row[4].toString()
                        val year = row[5].toString().toIntOrNull() ?: 0
                        val gpa = row[6].toString().toDoubleOrNull()
                        records.add(AcademicRecord(id, studentId, subject, grade, semester, year, gpa))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        records
    }

    override suspend fun getAcademicRecordsForStudent(studentId: String): List<AcademicRecord> {
        return getAllAcademicRecords().filter { it.studentId == studentId }
    }

    override suspend fun getAcademicRecordsForStudents(studentIds: List<String>): List<AcademicRecord> {
        return getAllAcademicRecords().filter { it.studentId in studentIds }
    }

    override suspend fun addAcademicRecord(record: AcademicRecord): Boolean = withContext(Dispatchers.IO) {
        try {
            val sheetsService = getSheetsService()
            val values = listOf(
                listOf(record.id, record.studentId, record.subject, record.grade, record.semester, record.year.toString(), record.gpa?.toString() ?: "")
            )
            val body = ValueRange().setValues(values)
            sheetsService.spreadsheets().values()
                .append(Constants.SPREADSHEET_ID, Constants.ACADEMIC_RECORDS_RANGE, body)
                .setValueInputOption("RAW")
                .execute()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateAcademicRecord(record: AcademicRecord): Boolean {
        // For simplicity, append as new record. In real app, update existing row.
        return addAcademicRecord(record)
    }
}