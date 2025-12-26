package com.school.app.data.repository

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.school.app.domain.model.Attendance
import com.school.app.domain.model.AttendanceStatus
import com.school.app.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AttendanceRepositoryImpl(private val context: Context) : AttendanceRepository {

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

    override suspend fun getAllAttendances(): List<Attendance> = withContext(Dispatchers.IO) {
        val attendances = mutableListOf<Attendance>()
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.ATTENDANCE_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values.drop(1)) { // Assuming first row is header
                    if (row.size >= 4) {
                        val id = row[0].toString()
                        val studentId = row[1].toString()
                        val date = row[2].toString()
                        val status = when (row[3].toString().uppercase()) {
                            "PRESENT" -> AttendanceStatus.PRESENT
                            "ABSENT" -> AttendanceStatus.ABSENT
                            "LATE" -> AttendanceStatus.LATE
                            else -> AttendanceStatus.ABSENT
                        }
                        attendances.add(Attendance(id, studentId, date, status))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        attendances
    }

    override suspend fun getAttendancesForStudent(studentId: String): List<Attendance> {
        return getAllAttendances().filter { it.studentId == studentId }
    }

    override suspend fun getAttendancesForStudents(studentIds: List<String>): List<Attendance> {
        return getAllAttendances().filter { it.studentId in studentIds }
    }

    override suspend fun markAttendance(attendance: Attendance): Boolean = withContext(Dispatchers.IO) {
        try {
            val sheetsService = getSheetsService()
            val values = listOf(
                listOf(attendance.id, attendance.studentId, attendance.date, attendance.status.name)
            )
            val body = ValueRange().setValues(values)
            sheetsService.spreadsheets().values()
                .append(Constants.SPREADSHEET_ID, Constants.ATTENDANCE_RANGE, body)
                .setValueInputOption("RAW")
                .execute()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}