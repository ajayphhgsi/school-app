# School Management App Architecture

## Overview
This document outlines the technical specifications for a Kotlin Android school management app. The app features a modern UI built with Jetpack Compose, a multi-role login system (admin, teacher, student), and uses Google Spreadsheets for data storage via the Google Sheets API.

## App Structure
The app follows MVVM architecture with clean separation of concerns.

- **data/**: Contains repositories, API clients, data transfer objects (DTOs), and local data sources.
- **domain/**: Includes business logic entities, use cases, and domain models.
- **presentation/**: Houses ViewModels, UI components (Jetpack Compose), and navigation logic.
- **utils/**: Utility classes, constants, and helper functions.

Key technologies:
- Kotlin
- Jetpack Compose for UI
- ViewModel and LiveData/StateFlow for state management
- Navigation Component for screen navigation
- Google APIs Client Library for Sheets integration

## Data Models
Data is stored in Google Spreadsheets and mapped to Kotlin data classes.

### Entities
- **User**: id (String), email (String), role (Role enum), name (String)
- **Student**: id (String), name (String), grade (String), fees (List<Fee>), academicHistory (List<AcademicRecord>), expenses (List<Expense>)
- **Teacher**: id (String), name (String), subjects (List<String>)
- **Admin**: Inherits from User with additional permissions
- **Fee**: id (String), studentId (String), amount (Double), dueDate (Date), status (FeeStatus enum)
- **AcademicRecord**: id (String), studentId (String), subject (String), grade (String), year (Int)
- **Expense**: id (String), studentId (String), description (String), amount (Double), date (Date)

Enums:
- Role: ADMIN, TEACHER, STUDENT
- FeeStatus: PENDING, PAID, OVERDUE

## UI Components
The app uses Jetpack Compose for declarative UI.

### Screens
- **LoginScreen**: Email/password input or Google Sign-In button
- **DashboardScreen**: Role-specific dashboard with navigation to features
- **StudentDetailsScreen**: View/edit student information
- **FeesScreen**: List of fees with payment status
- **AcademicHistoryScreen**: View grades and subjects
- **ExpensesScreen**: Track student expenses
- **UserManagementScreen** (Admin only): Manage users and roles

### Navigation Flow
Uses Navigation Component with NavHost.
- Login -> Dashboard (based on role)
- Dashboard -> Feature screens
- Conditional navigation based on permissions

## Role-Based Access Control
Access control is implemented at the ViewModel and UI levels.

- **Student**: Can view own details, fees, academic history, expenses
- **Teacher**: Can view assigned students, update grades, view fees
- **Admin**: Full access to all features, user management, data editing

Implementation:
- User role stored in session after login
- ViewModels check role before executing operations
- UI conditionally renders components based on role

## Google Sheets API Integration
Data storage uses Google Spreadsheets for simplicity and accessibility.

### Setup
- Google Cloud Project with Sheets API enabled
- OAuth 2.0 for authentication (Google Sign-In)
- Service account for server-side operations if needed

### Spreadsheet Structure
Single spreadsheet with multiple sheets:
- **Users**: User data
- **Students**: Student information
- **Fees**: Fee records
- **AcademicRecords**: Grade history
- **Expenses**: Expense tracking

### Integration Points
- **Authentication**: Google Sign-In for user login
- **Data Retrieval**: GET requests to read sheet ranges
- **Data Updates**: POST/PUT to append or update rows
- **Real-time Sync**: Periodic refresh or on-demand updates

API calls handled in Repository layer, mapped to domain models.

## Security Considerations
- OAuth for secure API access
- No sensitive data stored locally
- Role-based permissions prevent unauthorized access
- HTTPS for all API communications

## Performance
- Lazy loading for lists
- Caching of frequently accessed data
- Background sync for offline capabilities (if implemented)

## Testing
- Unit tests for ViewModels and Use cases
- Integration tests for API calls
- UI tests with Compose testing library

## Deployment
- APK generation via Gradle
- Google Play Store distribution
- Version management and updates