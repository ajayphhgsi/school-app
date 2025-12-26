package com.school.app.data.repository

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.school.app.domain.model.*
import com.school.app.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StudentRepositoryImpl(private val context: Context) : StudentRepository {

    private suspend fun getSheetsService(): Sheets = withContext(Dispatchers.IO) {
        val credential = GoogleCredential.fromStream(context.assets.open("service_account.json"))
            .createScoped(listOf(SheetsScopes.SPREADSHEETS_READONLY))
        Sheets.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("School App")
            .build()
    }

    override suspend fun getStudentById(id: String): Student? = withContext(Dispatchers.IO) {
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.STUDENTS_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values) {
                    if (row.size >= 6 && row[0].toString() == id) {
                        val name = row[1].toString()
                        val grade = row[2].toString()
                        val email = row[3].toString()
                        val phone = row[4].toString()
                        val address = row[5].toString()
                        val fees = getFeesForStudent(id)
                        val academicHistory = getAcademicRecordsForStudent(id)
                        val expenses = getExpensesForStudent(id)
                        return@withContext Student(id, name, grade, email, phone, address, fees, academicHistory, expenses)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    override suspend fun getAllStudents(): List<Student> = withContext(Dispatchers.IO) {
        val students = mutableListOf<Student>()
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.STUDENTS_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values.drop(1)) { // Assuming first row is header
                    if (row.size >= 6) {
                        val id = row[0].toString()
                        val name = row[1].toString()
                        val grade = row[2].toString()
                        val email = row[3].toString()
                        val phone = row[4].toString()
                        val address = row[5].toString()
                        val fees = getFeesForStudent(id)
                        val academicHistory = getAcademicRecordsForStudent(id)
                        val expenses = getExpensesForStudent(id)
                        students.add(Student(id, name, grade, email, phone, address, fees, academicHistory, expenses))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        students
    }

    override suspend fun getStudentsByTeacher(teacherId: String): List<Student> {
        // For simplicity, assume all students for now. In real app, link teachers to students.
        return getAllStudents()
    }

    private suspend fun getFeesForStudent(studentId: String): List<Fee> = withContext(Dispatchers.IO) {
        val fees = mutableListOf<Fee>()
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.FEES_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values.drop(1)) {
                    if (row.size >= 6 && row[1].toString() == studentId) {
                        val id = row[0].toString()
                        val type = row[2].toString()
                        val amount = row[3].toString().toDoubleOrNull() ?: 0.0
                        val dueDate = row[4].toString()
                        val status = when (row[5].toString().uppercase()) {
                            "PAID" -> FeeStatus.PAID
                            "OVERDUE" -> FeeStatus.OVERDUE
                            else -> FeeStatus.PENDING
                        }
                        fees.add(Fee(id, studentId, type, amount, dueDate, status))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fees
    }

    private suspend fun getAcademicRecordsForStudent(studentId: String): List<AcademicRecord> = withContext(Dispatchers.IO) {
        val records = mutableListOf<AcademicRecord>()
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.ACADEMIC_RECORDS_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values.drop(1)) {
                    if (row.size >= 5 && row[1].toString() == studentId) {
                        val id = row[0].toString()
                        val subject = row[2].toString()
                        val grade = row[3].toString()
                        val year = row[4].toString().toIntOrNull() ?: 0
                        records.add(AcademicRecord(id, studentId, subject, grade, year))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        records
    }

    private suspend fun getExpensesForStudent(studentId: String): List<Expense> = withContext(Dispatchers.IO) {
        val expenses = mutableListOf<Expense>()
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.EXPENSES_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values.drop(1)) {
                    if (row.size >= 5 && row[1].toString() == studentId) {
                        val id = row[0].toString()
                        val description = row[2].toString()
                        val amount = row[3].toString().toDoubleOrNull() ?: 0.0
                        val date = row[4].toString()
                        expenses.add(Expense(id, studentId, description, amount, date))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        expenses
    }
}