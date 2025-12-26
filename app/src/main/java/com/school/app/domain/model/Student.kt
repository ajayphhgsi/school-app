package com.school.app.domain.model

data class Student(
    val id: String,
    val name: String,
    val grade: String,
    val email: String,
    val phone: String,
    val address: String,
    val fees: List<Fee>,
    val academicHistory: List<AcademicRecord>,
    val expenses: List<Expense>
)

data class Fee(
    val id: String,
    val studentId: String,
    val type: String, // e.g., Tuition, Exam Fee, etc.
    val amount: Double,
    val dueDate: String, // Using String for simplicity, could be Date
    val status: FeeStatus
)

enum class FeeStatus {
    PENDING, PAID, OVERDUE
}

data class AcademicRecord(
    val id: String,
    val studentId: String,
    val subject: String,
    val grade: String,
    val semester: String,
    val year: Int,
    val gpa: Double?
)

data class Expense(
    val id: String,
    val studentId: String,
    val category: String,
    val description: String,
    val amount: Double,
    val date: String // Using String for simplicity
)

data class Attendance(
    val id: String,
    val studentId: String,
    val date: String, // YYYY-MM-DD
    val status: AttendanceStatus
)

enum class AttendanceStatus {
    PRESENT, ABSENT, LATE
}