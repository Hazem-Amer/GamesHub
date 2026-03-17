# GamesHub

GamesHub is a production-quality Android app that lets users browse video games by genre using the **IGDB API**.

## Overview

- Browse games by genre (IGDB `genres` filter)
- Infinite scroll with **manual pagination** (page -> IGDB offset)
- **Local search** (filters only already-loaded items in memory)
- Polished **Material 3** UI with dark mode and loading / error / empty states
- Game details screen (IGDB `games` query by id)

## Architecture

This project uses **Clean Architecture + MVVM** with a unidirectional state flow.

- **presentation**: Compose UI, navigation, ViewModels, immutable UI state (`StateFlow`)
- **domain**: business models + repository contracts + use-cases
- **data**: Retrofit API, DTOs + mappers, repository implementations

Data flow (simplified):

`Compose UI -> ViewModel -> UseCase -> Repository -> Retrofit -> IGDB API`

## Tech stack

- **Kotlin**, Coroutines, Flow
- **Jetpack Compose** + **Material 3**
- **Navigation Compose**
- **Retrofit2** + **OkHttp** + **Moshi**
- **Hilt** (DI)
- **Coil** (images)
- **JUnit4**, **Mockk**, **Turbine**, `kotlinx-coroutines-test`

## Project structure (single-module, scalable packages)

`app/src/main/java/com/example/gameshub/`

```
data/
  remote/
    api/
    dto/
    mappers/
  repository/
domain/
  model/
  repository/
  usecase/
di/
presentation/
  games/
    components/
  details/
  navigation/
  theme/
util/
```

## Build & run

1. Open the project in Android Studio.
2. Add your IGDB credentials.

### API configuration

The app reads credentials from Gradle properties and exposes them via `BuildConfig`:

- `IGDB_CLIENT_ID`
- `IGDB_CLIENT_SECRET`
- `IGDB_ACCESS_TOKEN`

Recommended (not committed):

- Add to `~/.gradle/gradle.properties`:

```
IGDB_CLIENT_ID=YOUR_CLIENT_ID
IGDB_CLIENT_SECRET=YOUR_CLIENT_SECRET
IGDB_ACCESS_TOKEN=YOUR_ACCESS_TOKEN
```

Alternative (project-local, do not commit to VCS):

- Add to the project `gradle.properties`:

```
IGDB_CLIENT_ID=YOUR_CLIENT_ID
IGDB_CLIENT_SECRET=YOUR_CLIENT_SECRET
IGDB_ACCESS_TOKEN=YOUR_ACCESS_TOKEN
```

Then build/run:

- `./gradlew test`
- `./gradlew assembleDebug`

## Assumptions

- Genres are loaded from IGDB (`/genres`) so IDs are always correct.
- If IGDB credentials are empty/invalid, network calls will fail and the app will show a retryable error state (the project still compiles).

## Future improvements

- Add a `/genres` endpoint and dynamic genre list.
- Add offline caching with Room and a network-bound resource.
- Add UI tests for navigation and list interactions.
- Add richer error mapping (timeouts, 401/403 handling) and better retry policies.

