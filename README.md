# 🎬 AnimeTracker — Android App

An Android app that tracks your favorite anime and sends notifications when new episodes are available. Uses **WorkManager** with **BootReceiver** to ensure background checks survive device reboots.

## ✨ Features

- 📋 Track favorite anime series
- 🔔 Get notified when new episodes drop
- 🔄 Background checking with WorkManager (every 1 hour)
- 🔁 Survives device reboots via BootReceiver
- 🛡️ Safety re-scheduling on app launch
- 📱 Android 13+ notification permission support

## 🏗️ Architecture

```
├── receiver/
│   └── BootReceiver.kt          # Re-schedules worker after reboot
├── worker/
│   └── AnimeWorker.kt           # Background episode checking
├── data/
│   └── FavoritesRepository.kt   # SharedPreferences persistence
├── network/
│   └── AnimeApiService.kt       # Jikan API integration
├── MainActivity.kt              # Entry point + WorkManager init
└── AndroidManifest.xml           # Permissions & receiver registration
```

## 🔧 How Boot Persistence Works

1. User adds anime to favorites
2. WorkManager schedules periodic checks (every 1 hour)
3. If phone reboots → `BootReceiver` catches `BOOT_COMPLETED`
4. BootReceiver re-schedules the WorkManager job
5. On next app launch, `MainActivity` also re-schedules (safety net)
6. `ExistingPeriodicWorkPolicy.KEEP` prevents duplicate jobs

## 📦 Dependencies

- WorkManager 2.9.0
- Jetpack Compose (Material 3)
- Gson 2.10.1
- Jikan API (MyAnimeList)

## 🚀 Getting Started

1. Clone the repo
2. Open in Android Studio
3. Sync Gradle
4. Run on device/emulator (API 24+)
jij
## 📝 License

MIT License — feel free to use and modify.
