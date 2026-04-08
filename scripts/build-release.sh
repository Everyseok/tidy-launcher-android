#!/bin/zsh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH="/opt/homebrew/opt/openjdk@17/bin:/opt/homebrew/bin:$PATH"
export ANDROID_SDK_ROOT="$ROOT_DIR/.android-sdk"
export GRADLE_USER_HOME="$ROOT_DIR/.gradle-home"
export TMPDIR="$ROOT_DIR/.tmp"

mkdir -p "$GRADLE_USER_HOME" "$TMPDIR"

if [[ -f "$ROOT_DIR/play-store/secrets/release-signing.env" ]]; then
  source "$ROOT_DIR/play-store/secrets/release-signing.env"
fi

cd "$ROOT_DIR"
./gradlew testDebugUnitTest assembleDebug bundleRelease --no-daemon
