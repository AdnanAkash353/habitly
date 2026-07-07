# Habitly Build Plan

Habitly is a production-quality Android daily habit tracker focused on helping people build consistent routines locally on their own device. Its core promise is that habit data, completion history, preferences, reminders, and app state remain fully offline: there is no backend, no account system, no tracking, no ads, no analytics, and no runtime dependency on any network connection.

## Fixed Technical Decisions

- **Package name:** `com.habitly.app`
- **Language:** Kotlin
- **UI:** Jetpack Compose with Material 3
- **Local storage:** Room / SQLite for habits and completions
- **Preferences storage:** Jetpack DataStore Preferences for onboarding completion and theme mode
- **Navigation:** Navigation-Compose
- **Architecture:** MVVM with ViewModels, `StateFlow` UI state, repositories wrapping Room DAOs and DataStore
- **Reminders:** Local notifications only, scheduled with inexact local scheduling such as `AlarmManager.setAndAllowWhileIdle` or WorkManager; no push messaging or FCM
- **Minimum SDK:** 26
- **Compile/target SDK:** Latest stable available in the build environment
- **Design system:** All screens must use the Phase 0.2 token files for colors, typography, shapes, spacing, elevation, and motion; no ad-hoc visual constants outside the token system
- **Fonts:** Manrope and Inter bundled locally under `res/font/`; no downloadable fonts or runtime font fetching
- **Permissions:** No permissions beyond notifications, and only when reminder functionality is implemented
- **External services:** No backend, network calls, login/auth, ads SDK, analytics SDK, crash-reporting SDK, cloud sync, or external database service
- **Dependencies:** Jetpack/AndroidX only unless a genuinely missing capability requires otherwise; nothing may require runtime network access or an account
- **Accessibility:** Minimum 48dp touch targets, meaningful content descriptions, TalkBack support for heatmap cells, 200% font-scale support, light/dark/system themes, and reduced-motion handling
- **Insets:** Handle status-bar, navigation-bar, bottom-navigation, and FAB insets explicitly because the target SDK will enforce edge-to-edge behavior

## Phase List

### Phase 0.1 — Goals, Plan & Blueprint
Create this `PLAN.md` at the project root, restating Habitly's local-only promise, documenting every build phase, recording fixed technical decisions, and resolving ambiguities without leaving open questions.

### Phase 0.2 — Layout Style & Color Design
Build only the design-token foundation: `Color.kt`, `Type.kt`, `Shape.kt`, `Motion.kt`, `Theme.kt`, bundled local font files, and a temporary preview-only `MainActivity` screen showing the color, type, and shape system in light and dark themes.

### Phase 1 — Basic App Skeleton
Replace the temporary design preview with the real navigation shell: every route from the spec, bottom navigation for Home / Stats / Settings, pushed Add/Edit and Detail routes, correct app-bar titles, themed blank bodies, and proper navigation transitions.

### Phase 2 — Frontend for Every Screen
Build the complete visual frontend for onboarding, Home, Add/Edit Habit, Habit Detail, Stats, and Settings using temporary sample data only, with interactions limited to no-ops or local Compose state.

### Phase 3 — Data Layer
Add the Room entities, DAOs, database, repository, DataStore preferences repository, `StreakCalculator`, and the required unit-test suite, without wiring real data into screens yet.

### Phase 4 — Home Screen Functionality
Replace sample Home data with `HomeViewModel` state from the repository and wire today's habit completion toggle to real `HabitCompletion` inserts/deletes with live streak updates and the specified completion animation.

### Phase 5 — Add / Edit Habit Functionality
Wire the Add/Edit form to real repository create, update, and delete operations, including name validation and the delete confirmation flow.

### Phase 6 — Habit Detail & Streak Functionality
Replace fake detail data with real habit and completion history, render the accurate 12-week heatmap, and compute current/best streaks through the tested `StreakCalculator`.

### Phase 7 — Stats Functionality
Replace fake Stats values with real aggregate weekly/monthly completion percentages and per-habit percentages that update when completions change.

### Phase 8 — Settings & Theme Functionality
Wire the Settings theme segmented control to the real `theme_mode` DataStore preference and apply the selected theme immediately across the entire app, persisting across restarts.

### Phase 9 — Reminders & Notifications Functionality
Implement Android notification permission handling, real local reminder scheduling, reminder notification delivery, Add/Edit reminder persistence, and live Settings notification permission state.

### Phase 10 — Onboarding Functionality
Wire onboarding to `has_completed_onboarding`, show it only on first launch, and remove it from the back stack after "Get Started" so system back never returns to onboarding from Home.

### Phase 11 — Final Polish & Definition of Done
Remove sample data, add the adaptive app icon and splash screen from the sprout motif, sweep empty states and edge cases, verify rotation/restart/background behavior, test both themes, large font scale, reduced motion, accessibility, and all hard constraints.

## Ambiguities and Resolutions

- **Android project scaffold is not present yet.** Phase 0.1 intentionally creates only `PLAN.md`; the Android project scaffold will be introduced in Phase 0.2 as the smallest step needed to host the theme preview.
- **Compile SDK and target SDK exact numbers depend on the environment.** Use the latest stable Android SDK available in the build environment when the Gradle project is created.
- **Reminder implementation allows either `AlarmManager` or WorkManager.** Prefer inexact local scheduling that avoids requesting `SCHEDULE_EXACT_ALARM`; choose the simpler option that integrates cleanly with habit-specific reminder times during Phase 9.
- **Bundled font source is not specified.** Add local Manrope and Inter font files under `res/font/` during Phase 0.2, avoiding any downloadable-font provider or runtime fetch.
- **Heatmap date labeling format is not fully prescribed.** Use literal, human-readable dates in accessibility descriptions, such as "March 3, completed," while retaining ISO strings internally for storage.
- **Color swatch alpha behavior is described visually, not as a fixed token.** Implement the habit emoji chip tint as the selected habit color at approximately 15% alpha through a reusable helper/component, keeping raw color values centralized in the design/token or preset definitions.
- **Manual habit reordering is represented by `sortOrder`, but no drag-and-drop UI is specified for v1.** Preserve and sort by `sortOrder`; do not add a reordering UI unless a later specification explicitly asks for it.
- **Testing beyond `StreakCalculator` is welcome but not phase-blocking.** Prioritize the required unit tests in Phase 3 and add UI tests only if they fit without expanding the phase scope.
