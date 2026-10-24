# Aurum Music — Native Android Music Player

A complete, production-grade native Android music player engineered from scratch with **Kotlin**, **Jetpack Compose**, **Material 3**, **Android Media3 ExoPlayer**, **Room Database**, and **Gradle**.

- **Package Name:** `com.aurummusic.player`
- **Application Name:** Aurum Music
- **Minimum Android Version:** Android 8.0 Oreo (API Level 26)
- **Target Android Version:** Android 15 (API Level 35)

---

## Architecture & Features

1. **Audio Engine (Media3 ExoPlayer):**
   - Low-latency local audio playback supporting MP3, FLAC, AAC, M4A, WAV, and OGG formats.
   - Gapless audio transition and audio focus handling (`AUDIOFOCUS_GAIN`, pausing on headset disconnect).
   - Sleep timer with auto-pause and fade-out countdown.

2. **Background Playback & Lock-Screen Controls:**
   - Dedicated `AurumMediaSessionService` extending Android's `MediaSessionService`.
   - Rich `MediaStyle` notification with play/pause, skip, rewind, forward, and live album artwork.
   - Prepared for **Android Auto** media browsing and vehicle playback via MediaLibraryService.

3. **MediaStore Audio Scanner:**
   - Full device scan querying `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI`.
   - Automatic extraction of track titles, artists, albums, durations, years, and artwork.
   - Grouping by Songs, Albums, Artists, and Storage Folders.

4. **Hardware DSP Equalizer:**
   - 5-Band parametric frequency sliders (-15 dB to +15 dB for 60Hz, 230Hz, 910Hz, 3.6kHz, 14kHz).
   - 8 Sound Presets: Flat, Bass Boost, Rock, Pop, Jazz, Electronic, Vocal Boost, Classical.
   - Native Android `BassBoost` and `Virtualizer` (3D Surround Sound) controls.

5. **Room Local Database:**
   - Persistent favorites (`FavoriteSongEntity`).
   - Custom user playlists (`PlaylistEntity`, `PlaylistSongCrossRef`) with M3U import & export.
   - Playback history (`PlaybackHistoryEntity`) tracking most played and recently played songs.

6. **Native Android Home-Screen Widget:**
   - AppWidget (`AurumWidgetProvider`) with album artwork, title, artist, and playback control broadcast intents.

7. **Jetpack Compose UI (Aesthetic & Responsive):**
   - Deep Black OLED/AMOLED background (`#0A0A0E`) with vibrant purple gradients (`#8B5CF6`, `#A855F7`) and gold accents (`#FACC15`).
   - Bottom navigation: **Home**, **Songs**, **Albums**, **Folders**, **Playlists**.
   - Floating Mini-Player bar with linear progress indicator and quick play/pause.
   - Full-screen **Now Playing** immersion with glowing vinyl artwork, 10s skip, shuffle, repeat, queue, and equalizer.

---

## How to Build the APK (`app-debug.apk`) Without Android Studio

You do **not** need Android Studio installed on your computer. Use either of the two methods below:

### Method 1: 1-Click Cloud Build with GitHub Actions (Recommended)
1. Push this project to any GitHub repository (public or private).
2. The preconfigured workflow at `.github/workflows/build-apk.yml` will automatically detect pushes or can be triggered manually under the **Actions** tab.
3. Once completed (~90 seconds), download `app-debug.apk` directly from the workflow **Artifacts** section!

### Method 2: Command Line Build (Windows / macOS / Linux)

Ensure you have **Java JDK 17** installed on your computer (`java -version`).

#### On Windows (PowerShell or Command Prompt):
```bat
.\gradlew.bat assembleDebug
```

#### On macOS or Linux:
```bash
chmod +x gradlew
./gradlew assembleDebug
```

### Output APK Location:
```
app/build/outputs/apk/debug/app-debug.apk
```

### Install onto your Android Phone via ADB:
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```
Or transfer `app-debug.apk` to your phone via USB cable, Google Drive, or messaging app and tap to install!
