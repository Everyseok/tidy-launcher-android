# QUERY_ALL_PACKAGES Justification

Tidy Launcher is a full Android launcher. Its core purpose is to display, search, organize, and launch installed apps from the user's home screen and app drawer.

The app needs package visibility so it can:

- Discover installed launchable apps
- Automatically classify them into folders
- Keep the home screen and app drawer organized when apps are added or removed
- Provide launcher search across installed apps

The app does not use package visibility for analytics, advertising, resale, or off-device profiling. Installed app metadata is processed locally on-device only.

