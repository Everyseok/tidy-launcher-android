#!/bin/zsh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
OUTPUT_DIR="$ROOT_DIR/play-store/assets/generated"

export ANDROID_SDK_ROOT="$ROOT_DIR/.android-sdk"

ADB_BIN="$ANDROID_SDK_ROOT/platform-tools/adb"
PACKAGE_NAME="com.tidylauncher.autoorganizer"
LAUNCHER_ACTIVITY="$PACKAGE_NAME/.ui.launcher.LauncherActivity"

mkdir -p "$OUTPUT_DIR"

"$ADB_BIN" wait-for-device

"$ADB_BIN" exec-out screencap -p > "$OUTPUT_DIR/device-onboarding.png"
"$ADB_BIN" shell cmd role add-role-holder --user 0 android.app.role.HOME "$PACKAGE_NAME" 0 || true
"$ADB_BIN" shell cmd package set-home-activity --user 0 "$LAUNCHER_ACTIVITY"
"$ADB_BIN" shell input keyevent 3
sleep 1
"$ADB_BIN" exec-out screencap -p > "$OUTPUT_DIR/device-launcher-home-actual.png"
