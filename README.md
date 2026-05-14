<p align="center">
  <img src="play-store/assets/generated/feature-graphic.png" alt="Tidy Launcher Feature Graphic" width="100%" />
</p>

<h1 align="center">Tidy Launcher</h1>

<p align="center">
  <strong>An Android launcher that automatically organizes installed apps into a cleaner home screen and app drawer.</strong>
</p>

<p align="center">
  Tidy Launcher scans installed apps on-device, classifies them by function and icon color, then generates a structured launcher layout with dock apps, priority apps, folders, and drawer sections.
</p>

<p align="center">
  <a href="https://github.com/Everyseok/tidy-launcher-android/releases/tag/v1.0.0">
    <img src="https://img.shields.io/badge/Latest%20Release-v1.0.0-204B57?style=for-the-badge" alt="Latest Release" />
  </a>
  <a href="https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-debug.apk">
    <img src="https://img.shields.io/badge/Download-APK-E08A45?style=for-the-badge&logo=android&logoColor=white" alt="Download APK" />
  </a>
  <a href="https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-release.aab">
    <img src="https://img.shields.io/badge/Download-AAB-1F2933?style=for-the-badge&logo=googleplay&logoColor=white" alt="Download AAB" />
  </a>
  <img src="https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Android-3DDC84?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
</p>

---

## What is Tidy Launcher?

**Tidy Launcher** is an Android launcher MVP focused on automatic app organization.

Instead of manually arranging dozens of icons, the app builds a launcher layout from installed-app metadata. It detects launchable apps, classifies them by function and dominant icon color, recommends an organization mode, and renders a clean launcher UI with home pages, folders, dock shortcuts, and a dual-view app drawer.

The project is designed as a real launcher, not just a normal utility app. After onboarding, the user can set Tidy Launcher as the default Home app.

---

## Preview

<p align="center">
  <img src="play-store/assets/generated/device-onboarding.png" alt="Tidy Launcher Onboarding" width="31%" />
  <img src="play-store/assets/generated/device-launcher-home-actual.png" alt="Tidy Launcher Home" width="31%" />
  <img src="play-store/assets/generated/phone-screenshot-3.png" alt="Tidy Launcher Screenshot" width="31%" />
</p>

<!--
If the third screenshot path does not exist yet, replace it with another generated screenshot under:
play-store/assets/generated/
-->

---

## Core Features

| Feature | Description |
|---|---|
| **Full Launcher Mode** | Registers as an Android Home launcher and can be selected as the default launcher. |
| **On-Device App Inventory** | Uses Android launcher APIs to detect installed launchable apps. |
| **Function-Based Classification** | Groups apps into categories such as work, finance, media, social, travel, tools, games, and utilities. |
| **Color-Based Classification** | Extracts dominant icon colors and groups apps by visual color families. |
| **Automatic Layout Planning** | Builds dock slots, priority apps, folders, home pages, and drawer sections. |
| **Recommendation Engine** | Recommends function/color organization and one-page/two-page layout based on app count and wallpaper context. |
| **Dual App Drawer** | Provides function-based and color-based app drawer views. |
| **Search & Launch** | Searches installed apps and launches them directly from the launcher UI. |
| **Onboarding Flow** | Guides the user through initial layout selection and Home role setup. |
| **Settings UI** | Allows rerunning layout generation and changing organization preferences. |
| **Package Change Refresh** | Reacts to installed/removed/changed apps and refreshes layout state. |
| **On-Device Privacy Model** | App metadata, classification, and layout decisions remain local to the device. |

---

## How It Works

```text
Installed apps
    ↓
LauncherApps / PackageManager inventory
    ↓
Room local database
    ↓
Function classifier + icon color classifier
    ↓
Recommendation engine
    ↓
Layout planner
    ↓
Dock + home pages + folders + drawer sections
    ↓
Jetpack Compose launcher UI

Tidy Launcher does not modify an OEM launcher in the background. It works as a replacement launcher after the user explicitly sets it as the default Home app.

Architecture
app/
├── data/
│   ├── AppInventoryProvider
│   └── local database / settings storage
├── domain/
│   ├── classification
│   ├── layout
│   ├── recommendation
│   └── model
├── platform/
│   ├── PackageChangeReceiver
│   └── RefreshLayoutWorker
├── ui/
│   ├── launcher
│   ├── onboarding
│   ├── settings
│   ├── common
│   └── theme
└── billing/

The app uses a lightweight dependency container to wire the database, settings repository, billing manager, classifiers, layout planner, recommendation engine, app inventory provider, and auto-arrange coordinator.

Tech Stack
Area	Stack
Language	Kotlin
UI	Jetpack Compose, Material 3
Architecture	MVVM-style ViewModels, repository/domain separation
App Inventory	LauncherApps, PackageManager
Local Data	Room, DataStore Preferences
Background Refresh	WorkManager
Icon Color Extraction	AndroidX Palette
Billing Placeholder	Google Play Billing
Build System	Gradle Kotlin DSL
Target SDK	Android 35
Minimum SDK	Android 30
Download
APK

Download tidy-launcher-debug.apk

AAB

Download tidy-launcher-release.aab

Release Page

Open latest release page

Install on Android
Download tidy-launcher-debug.apk.
Open the APK on your Android device.
If Android blocks installation, allow installs from your browser or file manager.
Launch Tidy Launcher.
Complete onboarding.
Set Tidy Launcher as the default Home app when prompted.
Build Locally
./gradlew clean
./gradlew testDebugUnitTest
./gradlew assembleDebug

Release bundle:

./gradlew bundleRelease

Helper scripts are available under scripts/:

scripts/build-release.sh
scripts/run-headless-emulator.sh
scripts/install-debug-apk.sh
scripts/capture-emulator-screenshots.sh
scripts/generate-play-assets.py
Current Status

Tidy Launcher is currently an Android launcher MVP with release artifacts available through GitHub Releases.

Implemented:

 Android launcher/home intent filters
 Jetpack Compose launcher UI
 Onboarding flow
 Settings screen
 Installed-app inventory
 Function-based app classification
 Icon color-based classification
 Recommendation engine
 Automatic layout planner
 Dock / folders / drawer sections
 Package change receiver
 WorkManager refresh hook
 Unit-testable classification and layout logic
 APK and AAB release artifacts

In progress / future work:

 Real-device UX polishing
 Launcher transition animations
 User-customizable pinned apps
 Folder editing
 More robust multilingual classification
 Store listing polish
 Production signing and Play Console release flow
 Premium feature finalization
Design Principles

Tidy Launcher is built around four rules:

Organize automatically
Reduce manual icon management.
Stay on-device
Installed-app metadata and classification results remain local.
Respect Android launcher behavior
Act as a real launcher only after the user selects it as Home.
Keep the layout readable
Use dock apps, priority apps, folders, and drawer sections instead of dumping every app onto one screen.
Policy Intent

Tidy Launcher is a user-selected launcher replacement.

It does not attempt to control or modify OEM launchers in the background. Its organization logic applies inside Tidy Launcher after the user explicitly launches it or selects it as the default Home app.

Repository Notes

Current release artifacts:

tidy-launcher-debug.apk
tidy-launcher-release.aab

Generated local outputs may include:

app/build/outputs/apk/debug/app-debug.apk
app/build/outputs/bundle/release/app-release.aab
app/build/reports/tests/testDebugUnitTest/index.html

Generated screenshots and Play Store assets may be located under:

play-store/assets/generated/
Author

Jun Seok Kim
Independent Researcher & AI Builder
GitHub: @Everyseok
