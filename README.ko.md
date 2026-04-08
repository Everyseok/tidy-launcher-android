# Tidy Launcher

안드로이드 홈 화면을 자동으로 정리해 주는 런처 MVP입니다. 설치된 앱을 읽어서 깔끔한 홈 화면과 이중 보기 앱서랍으로 재배치합니다.

English version: [README.md](README.md)

[![최신 릴리즈](https://img.shields.io/badge/Latest%20Release-v1.0.0-204B57?style=for-the-badge)](https://github.com/Everyseok/tidy-launcher-android/releases/tag/v1.0.0)
[![APK 다운로드](https://img.shields.io/badge/Download-APK-E08A45?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-debug.apk)
[![AAB 다운로드](https://img.shields.io/badge/Download-AAB-1F2933?style=for-the-badge&logo=googleplay&logoColor=white)](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-release.aab)

빠른 링크:

- [최신 릴리즈 페이지 열기](https://github.com/Everyseok/tidy-launcher-android/releases/tag/v1.0.0)
- [APK 바로 다운로드](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-debug.apk)
- [AAB 바로 다운로드](https://github.com/Everyseok/tidy-launcher-android/releases/download/v1.0.0/tidy-launcher-release.aab)

APK 설치 방법: `tidy-launcher-debug.apk`를 안드로이드 기기에 다운로드한 뒤 파일을 열고, 브라우저나 파일 관리자에서 설치 허용이 뜨면 승인한 다음 Tidy Launcher를 실행해 기본 홈 앱으로 설정하면 됩니다.

## 구현된 내용

- Jetpack Compose 기반 안드로이드 앱 스캐폴드와 런처/Home 인텐트 필터
- `LauncherApps` + `PackageManager` 기반 온디바이스 앱 인벤토리 수집
- 기능별 분류와 색상별 분류
- `기능/색상`, `1페이지/2페이지` 추천 엔진
- Dock, 우선 앱, 폴더, 앱서랍 섹션을 포함한 자동 레이아웃 플래너
- 온보딩 화면, 런처 UI, 설정 UI, 프리미엄 플레이스홀더
- 패키지 변경 감지 리시버와 `WorkManager` 재정리 훅
- 영어/한국어 문자열 리소스
- 분류기와 레이아웃 플래너용 순수 단위 테스트

## 현재 프로젝트 상태

- 빈 폴더에서 시작한 저장소를 앱 프로젝트 형태로 완성했습니다.
- 단위 테스트와 안드로이드 빌드는 로컬에서 검증 완료했습니다.
- 생성된 산출물:
  - `app/build/outputs/apk/debug/app-debug.apk`
  - `app/build/outputs/bundle/release/app-release.aab`
  - `app/build/reports/tests/testDebugUnitTest/index.html`
- 로컬 업로드 키 연동은 `play-store/secrets/release-signing.env` 기준으로 연결돼 있습니다.
- 프로젝트 내부 Android SDK에는 다음이 준비돼 있습니다:
  - `platforms;android-35`
  - `build-tools;35.0.0`
  - `platform-tools`
  - `emulator`
  - `cmdline-tools;latest`
  - `system-images;android-35;google_apis;arm64-v8a`

## 현재 데모 상태

- 프로젝트 내부 AVD 생성은 정상 동작합니다.
- macOS GUI 에뮬레이터는 패키징된 Qt `cocoa` 플러그인 문제 때문에 여전히 불안정합니다.
- 대신 헤드리스 에뮬레이터 부팅과 `adb` 설치/캡처 흐름은 끝까지 검증했습니다.
- 실제 에뮬레이터 스크린샷:
  - `play-store/assets/generated/device-onboarding.png`
  - `play-store/assets/generated/device-launcher-home-actual.png`

## 릴리즈 명령어

`scripts/` 안의 보조 스크립트를 사용하면 됩니다:

1. `scripts/build-release.sh`
2. `scripts/run-headless-emulator.sh`
3. `scripts/install-debug-apk.sh`
4. `scripts/capture-emulator-screenshots.sh`
5. `scripts/generate-play-assets.py`

## 정책 방향

- 사용자가 기본 홈 앱으로 설정한 이후 전체 런처로 동작합니다.
- 삼성/픽셀 기본 런처를 뒤에서 조작하지 않습니다.
- 설치 앱 메타데이터, 분류 결과, 레이아웃 결정은 모두 기기 안에만 남습니다.
