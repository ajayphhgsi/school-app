# School Management App

## App Overview

The School Management App is a comprehensive Android application designed to streamline school administration tasks. It provides role-based access for administrators, teachers, and students to manage student information, track attendance, handle fees, view academic records, and monitor expenses. The app uses Google Sheets as its backend for data storage, making it easy to access and edit data from anywhere.

## Features

- **Multi-Role Authentication**: Login system supporting Admin, Teacher, and Student roles with Google Sign-In integration.
- **Student Management**: View and edit student details, including personal information and enrollment status.
- **Attendance Tracking**: Mark and view attendance records for students.
- **Fee Management**: Track student fees, payment status, and due dates.
- **Academic Records**: View and update grades, subjects, and academic history.
- **Expense Tracking**: Monitor student-related expenses.
- **Grade Management**: Teachers can update student grades.
- **Dashboard**: Role-specific dashboards providing quick access to relevant features.

## Prerequisites

- Android Studio (version 4.0 or higher)
- JDK 8 or higher
- Google Cloud account for API setup
- Android device or emulator (API level 21 or higher)

## Google Sheets API Setup

Follow these step-by-step instructions to set up the Google Sheets API for the app.

### 1. Create a Google Cloud Project

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Click on the project dropdown and select "New Project".
3. Enter a project name (e.g., "School Management App") and click "Create".

### 2. Enable Google Sheets API

1. In the Google Cloud Console, navigate to "APIs & Services" > "Library".
2. Search for "Google Sheets API" and click on it.
3. Click "Enable" to activate the API for your project.

### 3. Create a Service Account

1. Go to "APIs & Services" > "Credentials".
2. Click "Create Credentials" > "Service Account".
3. Enter a service account name (e.g., "school-app-service").
4. Optionally, add a description and click "Create and Continue".
5. Skip the optional steps and click "Done".

### 4. Generate Service Account Key

1. In the "Credentials" page, find your newly created service account.
2. Click on the service account name.
3. Go to the "Keys" tab.
4. Click "Add Key" > "Create new key".
5. Select "JSON" as the key type and click "Create".
6. The JSON file will be downloaded automatically. Keep this file secure.

### 5. Set Up the Spreadsheet

1. Go to [Google Sheets](https://sheets.google.com) and create a new spreadsheet.
2. Rename the spreadsheet (e.g., "School Management Data").
3. Create the following sheets with the specified columns:

   - **Users** (Columns A-E):
     - A: ID
     - B: Email
     - C: Role (ADMIN, TEACHER, STUDENT)
     - D: Name
     - E: Password (hashed or plain for simplicity; use secure hashing in production)

   - **Students** (Columns A-I):
     - A: ID
     - B: Name
     - C: Grade
     - D: Email
     - E: Phone
     - F: Address
     - G: Parent Name
     - H: Enrollment Date
     - I: Status (ACTIVE, INACTIVE)

   - **Fees** (Columns A-F):
     - A: ID
     - B: Student ID
     - C: Amount
     - D: Due Date
     - E: Status (PENDING, PAID, OVERDUE)
     - F: Description

   - **AcademicRecords** (Columns A-G):
     - A: ID
     - B: Student ID
     - C: Subject
     - D: Grade
     - E: Year
     - F: Semester
     - G: Teacher

   - **Expenses** (Columns A-F):
     - A: ID
     - B: Student ID
     - C: Description
     - D: Amount
     - E: Date
     - F: Category

   - **Attendance** (Columns A-D):
     - A: ID
     - B: Student ID
     - C: Date
     - D: Status (PRESENT, ABSENT, LATE)

4. Share the spreadsheet with the service account email (found in the JSON file under "client_email") and give it "Editor" access.

## App Configuration

### 1. Update Constants.kt

1. Open `app/src/main/java/com/school/app/utils/Constants.kt`.
2. Replace `"your_spreadsheet_id_here"` with the actual ID from your Google Sheets URL (the long string between `/d/` and `/edit`).

### 2. Add Service Account JSON to Assets

1. Create the `app/src/main/assets/` directory if it doesn't exist.
2. Copy the downloaded service account JSON file into `app/src/main/assets/`.
3. Rename it to `service_account.json` (or update the code if using a different name).

## Building and Running the App

1. Clone or download the project to your local machine.
2. Open the project in Android Studio.
3. Ensure all dependencies are resolved by syncing the project with Gradle files.
4. Build the project:
   ```
   ./gradlew build
   ```
5. Run the app on an emulator or connected device:
   - In Android Studio, click the "Run" button or use `Shift + F10`.
   - Alternatively, use the command line:
     ```
     ./gradlew installDebug
     ```

## Usage Instructions

### General Usage

1. Launch the app and log in using your Google account or email/password.
2. The app will redirect you to a role-specific dashboard.

### For Students

- **View Profile**: Access your personal details and enrollment information.
- **Check Fees**: View outstanding fees and payment status.
- **Academic History**: Review your grades and subjects.
- **Expenses**: Monitor any expenses related to your account.

### For Teachers

- **Student List**: View students assigned to you.
- **Mark Attendance**: Record attendance for your classes.
- **Update Grades**: Enter or modify student grades in academic records.
- **View Fees**: Check fee status for students.

### For Administrators

- **Full Access**: Manage all students, users, fees, attendance, and academic records.
- **User Management**: Add, edit, or remove users and assign roles.
- **Data Editing**: Directly edit spreadsheet data through the app interface.
- **Reports**: Generate and view comprehensive reports on school data.

## Architecture and Technologies

The app follows the MVVM (Model-View-ViewModel) architecture pattern for clean separation of concerns:

- **Data Layer**: Repositories handle data operations with Google Sheets API.
- **Domain Layer**: Use cases encapsulate business logic.
- **Presentation Layer**: ViewModels manage UI state, and Jetpack Compose renders the UI.

**Key Technologies**:
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Components**: ViewModel, LiveData/StateFlow
- **Navigation**: Navigation Component
- **Networking**: Google APIs Client Library for Sheets
- **Authentication**: Google Sign-In
- **Build Tool**: Gradle

This setup ensures a scalable, maintainable, and user-friendly school management solution.
