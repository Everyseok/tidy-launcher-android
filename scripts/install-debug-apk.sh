#!/bin/zsh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

export ANDROID_SDK_ROOT="$ROOT_DIR/.android-sdk"

ADB_BIN="$ANDROID_SDK_ROOT/platform-tools/adb"
APK_PATH="$ROOT_DIR/app/build/outputs/apk/debug/app-debug.apk"
PACKAGE_NAME="com.tidylauncher.autoorganizer"

if [[ ! -f "$APK_PATH" ]]; then
  echo "Debug APK not found at $APK_PATH"
  exit 1
fi

"$ADB_BIN" wait-for-device
"$ADB_BIN" install -r "$APK_PATH"
"$ADB_BIN" shell monkey -p "$PACKAGE_NAME" -c android.intent.category.LAUNCHER 1
