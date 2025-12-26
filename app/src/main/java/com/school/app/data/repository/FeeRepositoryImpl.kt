package com.school.app.data.repository

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.school.app.domain.model.Fee
import com.school.app.domain.model.FeeStatus
import com.school.app.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FeeRepositoryImpl(private val context: Context) : FeeRepository {

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

    override suspend fun getAllFees(): List<Fee> = withContext(Dispatchers.IO) {
        val fees = mutableListOf<Fee>()
        try {
            val sheetsService = getSheetsService()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.FEES_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values.drop(1)) { // Assuming first row is header
                    if (row.size >= 6) {
                        val id = row[0].toString()
                        val studentId = row[1].toString()
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

    override suspend fun getFeesForStudent(studentId: String): List<Fee> {
        return getAllFees().filter { it.studentId == studentId }
    }

    override suspend fun getFeesForStudents(studentIds: List<String>): List<Fee> {
        return getAllFees().filter { it.studentId in studentIds }
    }
}