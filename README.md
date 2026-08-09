# 🩸 Diabetic Monitor — Android App

A complete Android app for diabetic glucose monitoring, built with modern Android architecture.

## ✨ Features

- **Glucose Logging** — 4 sessions: Fasting, After Breakfast (2hr), After Lunch (2hr), Before Dinner
  - Auto-timestamp + manual time picker
  - Color-coded: 🟢 Normal (70–140 mg/dL), 🔴 High, 🟡 Low
- **Medication Tracking** — Insulin (type, dose, time) & Tablet (name, dose, time)
- **Push Notifications** — WorkManager reminders for all sessions & medications
- **Charts** — Day / Month / Year views with MPAndroidChart + reference lines at 70–140 mg/dL
- **PDF Export** — Readings table, medication table, stats, doctor notes — shareable via share sheet
- **Light / Dark Theme** — Material Design 3

## 🏗️ Architecture

- **MVVM** + Repository pattern
- **Room** — `GlucoseReading`, `MedicationEntry`, `UserProfile`
- **Hilt** — Dependency injection
- **Jetpack Navigation** — Bottom navigation with 5 destinations
- **WorkManager** — Background notification scheduling
- **ViewBinding** — Type-safe view access

## 📱 Screens

| Home | History | Charts | Medication | Settings |
|------|---------|--------|-----------|---------|
| Today's 4 session cards | Filterable reading list | MPAndroidChart line chart | Medication list + mark taken | Profile + export PDF |

## ⚙️ Build

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34
- **Language**: Kotlin
- **Build System**: Gradle with KSP

## 🤖 CI/CD

GitHub Actions automatically builds the APK on every push to `main`.
See `.github/workflows/android.yml`.

## 📦 APK Download

Download the latest debug APK from the [GitHub Actions Artifacts](../../actions).
