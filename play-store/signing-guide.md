# Release Signing Guide

The current repository can build:

- signed debug APK
- unsigned release AAB by default

To produce a signed Play upload bundle, provide these values as Gradle properties or environment variables:

- `tidyReleaseStoreFile` or `TIDY_RELEASE_STORE_FILE`
- `tidyReleaseStorePassword` or `TIDY_RELEASE_STORE_PASSWORD`
- `tidyReleaseKeyAlias` or `TIDY_RELEASE_KEY_ALIAS`
- `tidyReleaseKeyPassword` or `TIDY_RELEASE_KEY_PASSWORD`

Example:

```bash
export TIDY_RELEASE_STORE_FILE=/absolute/path/to/upload-keystore.jks
export TIDY_RELEASE_STORE_PASSWORD=your-store-password
export TIDY_RELEASE_KEY_ALIAS=upload
export TIDY_RELEASE_KEY_PASSWORD=your-key-password
./gradlew bundleRelease --no-daemon
```

After those values are present, `bundleRelease` will emit a signed AAB instead of an unsigned one.

