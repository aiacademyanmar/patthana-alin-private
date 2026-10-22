# Paṭṭhāna Alin / ပဋ္ဌာန်း အလင်း — Native Android V2.3

This repository was initialized as **PUBLIC** on 2026-10-23, despite the name ending in `-private`.

## Important: make this repository private before uploading source and media

GitHub > Settings > General > Danger Zone > Change repository visibility > Make private.

The full Android project contains offline MP3 recordings and illustrated lesson assets. Do not publicly republish files without checking redistribution permission. The full source has **not** been uploaded to this public repository.

## Once visibility is Private

1. Download `Patthana_Alin_V2_3_GitHub_One_Click_APK_Builder.zip` from the ChatGPT conversation.
2. Extract it. Put the Android project files at repository root: `app/`, `settings.gradle.kts`, `build.gradle.kts`, `gradle.properties`, and `.github/workflows/`. Never upload keys, access tokens or `local.properties`.
3. Run the included `BUILD_APK_ON_GITHUB_WINDOWS.bat` with GitHub CLI authenticated, or upload the source using Git.
4. In Actions, run the Android APK workflow. Successful artifact: debug `app-debug.apk` to be renamed locally to `Patthana_Alin_Native_V2_3.apk`.
5. Inspect the Gradle job logs, APK checksum, signing and actual startup on an Android device.

## Features in the supplied Android source
- 24 illustrated Paṭṭhāna conditions
- 25 bundled offline recitation MP3 assets
- YouTube Dharma video catalog
- Sayadaw MP3 library index
- Google Drive eLibrary index (144 starter books)

**Build state:** No successful V2.3 APK has been generated in this repository yet. Merely naming a source ZIP `.apk` does not create an Android APK.
