# Tidy Launcher

Tidy Launcher is an Android launcher MVP that auto-organizes installed apps into a clean home screen and dual-view app drawer.

[![Latest Release](https://img.shields.io/badge/Latest%20Release-v1.0.0-204B57?style=for-the-badge)](https://github.com/Everyseok/tidy-launcher-android/releases/tag/v1.0.0)
[![Download APK](https://img.shields.io/badge/Download-APK-E08A45?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-debug.apk)
[![Download AAB](https://img.shields.io/badge/Download-AAB-1F2933?style=for-the-badge&logo=googleplay&logoColor=white)](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-release.aab)

Quick links:

- [Open the latest release page](https://github.com/Everyseok/tidy-launcher-android/releases/tag/v1.0.0)
- [Direct APK download](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-debug.apk)
- [Direct AAB download](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-release.aab)

## What is implemented

- Jetpack Compose Android app scaffold with launcher/home intent filters
- On-device app inventory using `LauncherApps` + `PackageManager`
- Functional and color-based classification
- Recommendation engine for `function vs color` and `1 page vs 2 pages`
- Automatic layout planning with dock, priority apps, folders, and drawer sections
- Onboarding flow, launcher UI, settings UI, and premium placeholders
- Package change receiver and `WorkManager` refresh hook
- English and Korean string resources
- Pure unit tests for classifiers and layout planning logic

## Current project status

- The repository was created from an empty folder and is now fully scaffolded.
- Unit tests and Android builds have already been validated locally.
- Generated artifacts are available at:
  - `app/build/outputs/apk/debug/app-debug.apk`
  - `app/build/outputs/bundle/release/app-release.aab`
  - `app/build/reports/tests/testDebugUnitTest/index.html`
- A local upload keystore is wired through `play-store/secrets/release-signing.env`.
- The project-local Android SDK now includes:
  - `platforms;android-35`
  - `build-tools;35.0.0`
  - `platform-tools`
  - `emulator`
  - `cmdline-tools;latest`
  - `system-images;android-35;google_apis;arm64-v8a`

## Current demo limits

- A project-local emulator AVD can now be created successfully.
- The macOS GUI emulator is still blocked by a Qt `cocoa` platform plugin issue in the packaged emulator runtime.
- Headless emulator boot works, and `adb`-based install plus screenshot capture are now working end-to-end.
- Real emulator screenshots are available at:
  - `play-store/assets/generated/device-onboarding.png`
  - `play-store/assets/generated/device-launcher-home-actual.png`

## Release commands

Use the helper scripts in `scripts/`:

1. `scripts/build-release.sh`
2. `scripts/run-headless-emulator.sh`
3. `scripts/install-debug-apk.sh`
4. `scripts/capture-emulator-screenshots.sh`
5. `scripts/generate-play-assets.py`

## Policy intent

- The app acts as a full launcher after the user sets it as the default Home app.
- It does not attempt to manipulate OEM launchers in the background.
- Installed app metadata, classifications, and layout decisions stay on-device.
