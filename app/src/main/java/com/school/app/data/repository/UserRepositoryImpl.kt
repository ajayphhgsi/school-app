package com.school.app.data.repository

import android.content.Context
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.school.app.domain.model.Role
import com.school.app.domain.model.User
import com.school.app.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(private val context: Context) : UserRepository {

    override suspend fun authenticate(email: String, password: String): User? = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleCredential.fromStream(context.assets.open("service_account.json"))
                .createScoped(listOf(SheetsScopes.SPREADSHEETS_READONLY))
            val sheetsService = Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
            )
                .setApplicationName("School App")
                .build()
            val response = sheetsService.spreadsheets().values()
                .get(Constants.SPREADSHEET_ID, Constants.USERS_RANGE)
                .execute()
            val values = response.getValues()
            if (values != null) {
                for (row in values) {
                    if (row.size >= 5) {
                        val userEmail = row[1].toString()
                        val userPassword = row[2].toString()
                        val roleStr = row[3].toString()
                        val name = row[4].toString()
                        val id = row[0].toString()
                        if (userEmail == email && userPassword == password) {
                            val role = when (roleStr.uppercase()) {
                                "ADMIN" -> Role.ADMIN
                                "TEACHER" -> Role.TEACHER
                                "STUDENT" -> Role.STUDENT
                                else -> Role.STUDENT
                            }
                            return@withContext User(id, email, password, role, name)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }
}