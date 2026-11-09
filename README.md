# Money Tracker (Android)

A simple, private income & expense tracker built with Kotlin, Jetpack Compose and Room.
All data stays on the device.

## Features
- Add, edit and delete income and expense transactions (swipe left on a row to delete, with Undo)
- Browse by month, with balance / income / expense summary
- Custom categories: choose your own name, color and icon (16 starter categories included)
- Reports: category breakdown donut chart and a 6‑month income vs. expense chart
- Themes: 6 color themes (Ocean, Forest, Sunset, Royal, Rose, Midnight), light / dark / system mode,
  and Material You (wallpaper colors) on Android 12+
- Choose your currency (defaults to EGP)

## Build and run
1. Install Android Studio (Ladybug or newer) and open the `MoneyTracker` folder.
2. Let Gradle sync (it downloads the Gradle wrapper and dependencies).
3. Pick a device or emulator (Android 8.0 / API 26 or higher) and press Run.

To produce an APK for your phone: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
The debug APK ends up in `app/build/outputs/apk/debug/`.

## Project layout
- `data/` – Room entities, DAOs, database, DataStore settings
- `ui/theme/Theme.kt` – the color palettes and theme setup (add your own palette to `Palettes`)
- `ui/*Screen.kt` – Home, Add/Edit, Reports, Categories, Settings
- `MainActivity.kt` – navigation and bottom bar

## Build the APK without Android Studio (GitHub Actions)
1. Create a free GitHub account and a new repository.
2. Upload everything in this folder to it, including the hidden `.github` folder (on the repo page: Add file > Upload files, then drag the whole folder contents in).
3. Open the repository's **Actions** tab. The "Build APK" workflow runs automatically (or click **Run workflow**).
4. When it finishes (about 5-10 minutes), open the run and download **MoneyTracker-debug-apk** from the Artifacts section. Unzip it and install the `.apk` on your phone.
