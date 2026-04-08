#!/bin/zsh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
EMULATOR_LOG_DIR="$ROOT_DIR/.tmp"
EMULATOR_LOG_FILE="$EMULATOR_LOG_DIR/tidy-emulator.log"

export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH="/opt/homebrew/opt/openjdk@17/bin:/opt/homebrew/bin:$PATH"
export ANDROID_SDK_ROOT="$ROOT_DIR/.android-sdk"
export ANDROID_USER_HOME="$ROOT_DIR/.android-home"
export ANDROID_AVD_HOME="$ROOT_DIR/.android-home/avd"
export TMPDIR="$ROOT_DIR/.tmp"

mkdir -p "$ANDROID_USER_HOME" "$ANDROID_AVD_HOME" "$TMPDIR"

if [[ ! -f "$ANDROID_AVD_HOME/TidyPhone.ini" ]]; then
  "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" create avd \
    -n TidyPhone \
    -k "system-images;android-35;google_apis;arm64-v8a" \
    --device pixel_8 \
    -p "$ANDROID_AVD_HOME/TidyPhone.avd"
fi

cd "$ROOT_DIR"

"$ANDROID_SDK_ROOT/emulator/emulator" \
  -avd TidyPhone \
  -no-window \
  -no-snapshot \
  -wipe-data \
  -no-boot-anim \
  -gpu swiftshader_indirect \
  -crash-report-mode disabled \
  >"$EMULATOR_LOG_FILE" 2>&1 &

echo "$!" > "$EMULATOR_LOG_DIR/tidy-emulator.pid"
echo "Headless emulator started. Log: $EMULATOR_LOG_FILE"
