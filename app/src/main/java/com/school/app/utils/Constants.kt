package com.school.app.utils

object Constants {
    const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets/"
    const val SPREADSHEET_ID = "your_spreadsheet_id_here" // Replace with actual ID
    const val USERS_RANGE = "Users!A:E"
    const val STUDENTS_RANGE = "Students!A:I" // Assuming columns: id, name, grade, email, phone, address, and then lists are separate sheets
    const val FEES_RANGE = "Fees!A:F"
    const val ACADEMIC_RECORDS_RANGE = "AcademicRecords!A:G"
    const val EXPENSES_RANGE = "Expenses!A:F"
    const val ATTENDANCE_RANGE = "Attendance!A:D"
}