# GamesHub

GamesHub is an Android app that lets users browse video games by genre using the **IGDB API instead of RAWG since RAWG API is not working since 2024**, with a polished Netflix‑style UI and **offline‑first** caching.

## Overview

- Browse games by genre (IGDB `genres` filter).
- Infinite scroll with **manual pagination** (page → IGDB offset).
- **Local search** that filters only already loaded items in memory (no extra API calls).
- Game details screen with screenshots and trailers when available.
- **Offline‑first** behaviour: previously loaded lists and details remain available without network.
- Dark/light themes, empty and error states, and animated splash screen.

## 📦 Download APK
[⬇️ Download GamesHub APK](./app-debug.apk)

## Tech stack

- **Language & concurrency**
  - Kotlin
  - Kotlin Coroutines & Flow
- **UI**
  - Jetpack Compose
  - Material 3
  - Navigation Compose
  - Coil (image loading)
- **Architecture & DI**
  - Clean Architecture (data / domain / presentation)
  - MVVM (ViewModel + immutable UI state with `StateFlow`)
  - Hilt (Dagger Hilt) for dependency injection
- **Data & networking**
  - Retrofit2
  - OkHttp (with interceptors)
  - Moshi (JSON)
  - Room (offline cache for games list, details, and genres)
- **Testing**
  - JUnit4
  - Mockk
  - Turbine
  - `kotlinx-coroutines-test`

## Architecture choice (MVVM, offline‑first)

MVVM was chosen over MVI because:

- It matches the standard Android guidance and is familiar to most Android developers.
- It works naturally with `ViewModel` + `StateFlow` and Compose, without needing a full intent/reducer setup.
- It keeps the codebase simple while still enforcing a **single source of truth** for UI state.

The data flow is:

`Compose UI → ViewModel → UseCase → Repository → (Room + Retrofit IGDB API) → Repository → UseCase → ViewModel → UI`

The repository is **offline‑first**:

- Room is the primary source of truth.
- If cached data exists, it is returned immediately.
- Network is only used to refresh the cache; when offline and no cache exists, use cases surface errors that the UI renders as error states.

## Build & run

1. **Open the project** in Android Studio (Giraffe or newer).
2. **Configure IGDB credentials**.

### API configuration

The app reads credentials from Gradle properties and exposes them via `BuildConfig`:

- `IGDB_CLIENT_ID`
- `IGDB_CLIENT_SECRET`
- `IGDB_ACCESS_TOKEN`

Recommended (not committed):

Add the following to your global Gradle properties file (`~/.gradle/gradle.properties` on macOS/Linux/Windows):

```properties
IGDB_CLIENT_ID=YOUR_CLIENT_ID
IGDB_CLIENT_SECRET=YOUR_CLIENT_SECRET
IGDB_ACCESS_TOKEN=YOUR_ACCESS_TOKEN
```


Add the same keys to the project‑level `gradle.properties`:

```properties
IGDB_CLIENT_ID=YOUR_CLIENT_ID
IGDB_CLIENT_SECRET=YOUR_CLIENT_SECRET
IGDB_ACCESS_TOKEN=YOUR_ACCESS_TOKEN
```

### Build commands

From the project root:

```bash
./gradlew test
./gradlew assembleDebug
```

Both commands succeed with the current configuration, so the project builds successfully from the instructions above.

## Assumptions and shortcuts

- **IGDB credentials**: The app assumes valid IGDB client ID/secret/access token. If they are missing or invalid, network requests will fail and the app will surface a user‑friendly error state, but the project still compiles and runs.
- **Offline‑first scope**: Offline caching is implemented for:
  - Games list pages (by page + genre).
  - Individual game details.
  - Genres list.
  It does not implement background sync, eviction policies, or conflict resolution—those are out of scope for this sample.
- **Error handling**: Errors are mapped to generic “network/server/unexpected error” messages instead of fine‑grained types (e.g., 401 vs 403 vs 404) to keep the code focused on architecture rather than exhaustive error UX.
- **UI coverage**: Core flows (games list, details, search, pagination, offline) are implemented and tested at the unit level. Full end‑to‑end UI tests and accessibility audits are not included due to time.
- **Performance**: Manual pagination is used instead of Paging 3 to keep dependencies and concepts simple, while still demonstrating infinite scroll and in‑memory search.

These trade‑offs keep the project understandable and maintainable as a reference app while still being realistic enough for production‑style code.

## 🎥 Demo Video
[![Watch Demo](https://img.youtube.com/vi/zPlIJJb-Ezc/0.jpg)](https://www.youtube.com/watch?v=zPlIJJb-Ezc)