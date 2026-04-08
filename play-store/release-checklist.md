# Tidy Launcher Release Checklist

## Build artifacts already generated

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`
- Unit test report: `app/build/reports/tests/testDebugUnitTest/index.html`
- Real emulator screenshots:
  - `play-store/assets/generated/device-onboarding.png`
  - `play-store/assets/generated/device-launcher-home-actual.png`
- Generated store graphics:
  - `play-store/assets/generated/feature-graphic-1024x500.png`
  - `play-store/assets/generated/play-icon-512.png`
  - `play-store/assets/generated/screenshot-home-auto-organize.png`
  - `play-store/assets/generated/screenshot-color-layouts.png`
  - `play-store/assets/generated/screenshot-privacy-local.png`

## Before Play Console upload

1. Replace the privacy policy contact section with a real support email.
2. Upload `app/build/outputs/bundle/release/app-release.aab` through a real Play Console account.
3. Review whether the generated screenshots/graphics are sufficient or if you want device-polished variants.
4. Paste the metadata from `play-store/metadata/en-US` and `play-store/metadata/ko-KR` into Play Console.
5. Use `play-store/data-safety.md` to complete the Data safety form.
6. Use `play-store/query-all-packages-justification.md` when explaining launcher-related package visibility usage.

## Not yet completed in this repository

- Play Console upload
- Support email / website
- macOS GUI emulator window fix
