# Google Sheets API Setup Guide

This guide provides step-by-step instructions for setting up the Google Sheets API, which is **compulsory** before building and running the School Management App. The app uses Google Sheets as its data storage backend, so proper API configuration is essential for functionality.

## Prerequisites

- Google Account
- Internet connection
- Android Studio or compatible build environment
- Basic knowledge of Google Cloud Console

## Step 1: Create a Google Cloud Project

1. Go to the [Google Cloud Console](https://console.cloud.google.com/).
2. Click on the project dropdown at the top and select "New Project".
3. Enter a project name (e.g., "School Management App") and click "Create".
4. Wait for the project to be created and select it from the project dropdown.

## Step 2: Enable Google Sheets API

1. In the Google Cloud Console, navigate to "APIs & Services" > "Library".
2. Search for "Google Sheets API" and click on it.
3. Click "Enable" to activate the API for your project.

## Step 3: Create Service Account Credentials

1. Go to "APIs & Services" > "Credentials".
2. Click "Create Credentials" > "Service Account".
3. Enter a service account name (e.g., "school-app-service").
4. Optionally, add a description and click "Create and Continue".
5. Skip the optional steps by clicking "Done".
6. In the "Credentials" section, find your new service account and click on it.
7. Go to the "Keys" tab and click "Add Key" > "Create new key".
8. Select "JSON" as the key type and click "Create".
9. The JSON file will be downloaded automatically. **Keep this file secure and do not share it.**

## Step 4: Set Up Google Spreadsheet

1. Go to [Google Sheets](https://sheets.google.com) and create a new spreadsheet.
2. Rename the spreadsheet to something meaningful (e.g., "School Management Data").
3. Create the following sheets (tabs) with exact names and column headers:

### Users Sheet
- Columns: id, email, password, role, name

### Students Sheet
- Columns: id, name, grade, email, phone, address

### Fees Sheet
- Columns: id, studentId, type, amount, dueDate, status

### AcademicRecords Sheet
- Columns: id, studentId, subject, grade, semester, year, gpa

### Expenses Sheet
- Columns: id, category, amount, date, description

### Attendance Sheet
- Columns: id, studentId, date, status

4. Note the Spreadsheet ID from the URL (the long string between `/d/` and `/edit`).

## Step 5: Configure App Credentials

1. In your project directory, create an `app/src/main/assets` folder if it doesn't exist.
2. Copy the downloaded JSON file from Step 3 into `app/src/main/assets/` and rename it to `service_account.json`.
3. Open `app/src/main/java/com/school/app/utils/Constants.kt`.
4. Update the `SPREADSHEET_ID` constant with your actual spreadsheet ID from Step 4.

```kotlin
const val SPREADSHEET_ID = "your_actual_spreadsheet_id_here"
```

## Step 6: Share Spreadsheet with Service Account

1. Go back to your Google Spreadsheet.
2. Click "Share" button.
3. In the "Add people and groups" field, enter the service account email (found in the JSON file as "client_email").
4. Set the permission to "Editor" and click "Send".

## Step 7: Verify Setup

Before building the app:

1. Ensure the `service_account.json` file is in `app/src/main/assets/`.
2. Confirm `SPREADSHEET_ID` in `Constants.kt` is updated.
3. Check that the spreadsheet is shared with the service account email.
4. Run the build script (`./build.sh`) or use Android Studio to build the app.

## Troubleshooting

- **Build fails with API errors**: Double-check the JSON file placement and spreadsheet sharing.
- **Data not loading**: Verify spreadsheet ID and sheet names match exactly.
- **Authentication issues**: Ensure the service account has Editor permissions on the spreadsheet.

## Security Notes

- Never commit the `service_account.json` file to version control.
- Add `service_account.json` to your `.gitignore` file.
- Regularly rotate service account keys for security.

Once these steps are completed, you can successfully build and run the School Management App.