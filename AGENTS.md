# Repository Guidelines

## Project Structure & Module Organization
- App code lives in `app/src/main/java/de/leipsfur/kcal_track/`.
- UI is organized by feature under `ui/` (`dashboard/`, `food/`, `activity/`, `weight/`, `settings/`, `navigation/`, `theme/`).
- Data layer is under `data/` (`db/` for Room entities/DAOs/database, `repository/` for repository classes).
- Widget code is in `widget/` (Glance AppWidget).
- Resources are in `app/src/main/res/` and DB schemas in `app/schemas/`.
- Unit tests: `app/src/test/`; instrumented tests: `app/src/androidTest/`.
- Product/architecture docs: `docs/stories/`, `docs/arch/`, `docs/todo/`.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` builds a debug APK.
- `./gradlew test` runs JVM unit tests.
- `./gradlew connectedAndroidTest` runs instrumentation tests on a device/emulator.
- `./gradlew lint` runs Android lint checks.
- `./gradlew :app:installDebug` installs debug build on a connected device.

WSL setup (required to avoid `JAVA_HOME is not set`):
- `export JAVA_HOME=/usr/lib/jvm/java-25`
- `export ANDROID_HOME=/mnt/c/Users/darky/AppData/Local/Android/Sdk`
- `export PATH=$JAVA_HOME/bin:$PATH`
- Verify with `java -version` and `echo $JAVA_HOME`.

If Android SDK tooling still fails in WSL, run builds from Android Studio or PowerShell.

## Coding Style & Naming Conventions
- Language: Kotlin only; UI in Jetpack Compose.
- Follow standard Kotlin formatting (4-space indentation, no tabs).
- Naming: `PascalCase` for classes/composables, `camelCase` for functions/vars, `UPPER_SNAKE_CASE` for constants.
- Keep feature boundaries clear: screen + `ViewModel` + state per feature package.
- Suffix patterns used in repo: `*Screen`, `*ViewModel`, `*Repository`, `*Dao`.
- Prefer string resources in `res/values/strings.xml` (German default).

## Testing Guidelines
- Frameworks: JUnit4, MockK, `kotlinx-coroutines-test`; AndroidX test + Espresso for instrumentation.
- Place tests beside the feature they cover (e.g., `ui/dashboard/DashboardViewModelTest.kt`).
- Test names should describe behavior, e.g., `saveEntry_updatesUiState()`.
- Add/adjust tests for any changed business logic in `ViewModel`, repository, or DAO code.

## Commit & Pull Request Guidelines
- Current history uses concise, imperative subjects (e.g., `Improve accessibility (Phase 10)`).
- Commit message format: `Short imperative summary` (optional phase/context in parentheses).
- PRs should include:
  - What changed and why.
  - Linked issue/story reference.
  - Test evidence (`./gradlew test`, device/emulator checks).
  - Screenshots/GIFs for UI changes (especially Compose screens/widget).
