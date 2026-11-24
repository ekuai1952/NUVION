# NUVION — Атомдық & Ядролық Физика

Native Android learning app (Kotlin + Jetpack Compose + Material 3) built from the NUVION Figma mock-up.
Package: `kz.nuvion.app`. Languages: Kazakh (default), Russian, English.

## Running the project

1. Install **Android Studio** (Ladybug 2024.2 or newer). It ships its own JDK 17 and the Android SDK.
2. `File → Open…` and choose this `Nuvion` folder.
3. Let Gradle sync finish. Studio downloads Gradle 8.11.1 (from `gradle/wrapper/gradle-wrapper.properties`),
   the Android Gradle Plugin and all libraries.
   - The `gradlew` scripts and `gradle-wrapper.jar` are not included. If Studio asks, accept its offer to create
     the wrapper, or run `gradle wrapper --gradle-version 8.11.1` once in this folder.
4. Create an emulator (`Device Manager → Pixel, API 35`) or connect a phone with USB debugging.
5. Press **Run ▶** (configuration `app`).

Unit tests: `./gradlew test` (or right-click `app/src/test` → Run tests).

## Tech stack and versions

| Area | Library | Version |
|---|---|---|
| Build | Android Gradle Plugin / Gradle | 8.7.3 / 8.11.1 |
| Language | Kotlin (+ Compose compiler plugin), KSP | 2.1.0 / 2.1.0-1.0.29 |
| SDK | minSdk / targetSdk / compileSdk | 26 / 35 / 35 |
| UI | Compose BOM (Material 3, icons-extended) | 2024.12.01 |
| Navigation | navigation-compose | 2.8.5 |
| Lifecycle | lifecycle-viewmodel/runtime-compose | 2.8.7 |
| Storage | Room (KSP) / DataStore Preferences | 2.6.1 / 1.1.1 |
| Async | kotlinx-coroutines-android | 1.9.0 |
| AR | ARCore client (support check only) | 1.44.0 |

All versions are in `gradle/libs.versions.toml`. To move to newer releases, update them there and re-sync.
No paid libraries or services are used.

## Project structure

```
app/src/main/java/kz/nuvion/app/
├── NuvionApplication.kt, AppContainer.kt (manual DI), MainActivity.kt
├── domain/
│   ├── model/        Topic, Lesson, Quiz, Question, AnswerOption, PracticeProblem,
│   │                 LibraryMaterial, Mission, UserProgress, ArModel, LocalizedText
│   ├── repository/   ContentRepository, ProgressRepository, SettingsRepository (interfaces)
│   └── usecase/      MissionCalculator (unlock rules, "Continue" target)
├── data/
│   ├── demo/         Demo content: 5 topics × (lesson, 5 questions, 3 problems), library
│   ├── source/       ContentDataSource (+ LocalContentDataSource)
│   ├── repository/   ContentRepositoryImpl, ProgressRepositoryImpl
│   └── local/        database/ (Room: entities, DAO, DB), datastore/ (settings)
├── viewmodel/        One ViewModel per screen + AppViewModelProvider factory
├── navigation/       Routes, NuvionNavHost
├── ui/
│   ├── theme/        Colors, typography, shapes, adaptive dimensions
│   ├── components/   Buttons, header, cards, progress, atom illustrations, Professor Proton
│   └── screens/      home, profile, topics, lesson, quiz, practice, library, missions, arlab, result
└── util/             UiState, AnswerChecker, ArAvailabilityChecker
```

## Navigation routes

`home`, `profile`, `topics`, `lesson/{topicId}`, `quiz/{topicId}`, `practice/{topicId}`, `library`, `library/{materialId}`,
`missionMap`, `arLab` (optional `?model=`), `result/{type}/{topicId}` (`type` = `quiz` | `practice` | `lesson`).

## What is stored where

- **Room** (`nuvion.db`): completed lessons, quiz results (last/best score, attempts, whether XP was given),
  solved problems, unlocked missions, total XP, current topic, last opened screen.
- **Room** also keeps an XP history (`xp_events`) for the profile's weekly chart and streak.
- **DataStore** (`nuvion_settings`): language, first-launch flag, haptic feedback setting, learner's name.

## Profile

"Жалғастыру" on the home screen opens the profile (as in the mock-up): name (editable), level
(200 XP per level), progress to the next level, XP per day this week, 6 achievements unlocked by real
progress, statistics (topics, quiz accuracy, streak) and a "Continue learning" button that opens the
current lesson.

XP rules: lesson +50 once; quiz +100 once, on the first attempt with ≥ 60 % correct; each problem +30 once.
A mission unlocks when the previous topic's lesson, quiz and all problems are complete.

## Languages

`res/values` = Kazakh (default), `res/values-ru`, `res/values-en`. The switch on the home screen changes the language
instantly without restarting the activity (`ui/LocalizedContent.kt`) and saves it to DataStore.
Learning content (lessons, questions, problems) carries all three translations in `LocalizedText`.

## Connecting a server later

`ContentRepository` reads from a `ContentDataSource`. Write a `RemoteContentDataSource` (Retrofit, Ktor or Firebase)
that implements the same interface and pass it in `AppContainer`. Screens and ViewModels do not change.

## AR and 3D models

See `app/src/main/assets/models/README.md`. Without ARCore or GLB files, the AR lab shows an interactive 3D model
(drag to rotate, pinch to zoom, double-tap to reset) and a message that AR is unavailable.

## Font

The mock-up uses Inter. The app uses the system sans-serif by default; to switch to Inter, add the TTF files to
`res/font` and update `ui/theme/Type.kt` (instructions in that file).
