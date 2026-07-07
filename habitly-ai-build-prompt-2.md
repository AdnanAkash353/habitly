# Habitly — Build Specification

## 1. Role & Operating Instructions

You are building a complete, production-quality Android application called **Habitly** — a local-only daily habit tracker with no backend, no accounts, and no dependency on any network connection. This document is the full specification: tech stack, design system, data model, screen-by-screen UI/UX, navigation, and a phased build plan. Build exactly what's specified here.

1. Work through the **Build Phases** in §7 strictly in order, one phase per turn. They are intentionally narrow — never combine two phases into a single turn, even where it would feel more efficient; the narrowness is what keeps each step accurate.
2. At the end of each phase: list every file you created or changed, state in one or two sentences what now works, confirm the project compiles and runs, then stop and wait for confirmation before starting the next phase.
3. Do not skip ahead into a later phase's scope, and do not add any feature, screen, or dependency that isn't specified somewhere in this document.
4. Treat the **Design System** in §3 as part of the specification, not optional polish — apply its color, type, shape, spacing, and motion tokens to every screen you build in §5.
5. Never introduce a backend, network call, login/auth system, analytics SDK, ads SDK, or any other runtime dependency on an external service — the full list is in §9.
6. Where something is ambiguous or underspecified, make the smallest reasonable decision consistent with the rest of this document, note that assumption in your phase summary, and keep moving rather than stopping to ask.

---

## 2. Tech Stack

- Language: Kotlin
- UI: Jetpack Compose (Material 3)
- Local storage: Room (SQLite) for habits and completions; Jetpack DataStore (Preferences) for lightweight app settings — no network, no cloud sync, no external database service
- Navigation: Navigation-Compose
- Reminders: local notifications only (`AlarmManager` inexact scheduling or `WorkManager`, §7 Phase 9) — no push/FCM
- Architecture: MVVM — ViewModel + StateFlow per screen, a Repository layer wrapping the Room DAOs and the DataStore preferences store
- Package name: `com.habitly.app`
- minSdk 26, compileSdk/targetSdk: latest stable your environment defaults to
- Because targetSdk will be recent enough to enforce edge-to-edge display by default, handle window insets explicitly anywhere content could sit under the status bar or gesture-nav bar — top app bars, the bottom navigation bar, and the FAB all need correct inset padding rather than relying on plain defaults
- Testing: JUnit for `StreakCalculator` (required cases in §4); Compose UI tests are welcome but not phase-blocking
- Dependencies limited to Jetpack/AndroidX plus the two bundled font families in §3.3 — nothing else third-party unless genuinely missing from it, and nothing that requires a runtime network call or account, ever

---

## 3. Design System

Habits are things you grow; a streak is how far something has grown. Every decision below should reinforce that idea instead of falling back on generic Material defaults. Two places carry all of the app's visual personality — the habit-detail heatmap (§3.1, §5.4) and the complete-habit tap gesture (§3.6). Everything else stays quiet and legible so those two moments stand out.

Build the full token system into a real Compose theme in Phase 0.2 (`ui/theme/Color.kt`, `Type.kt`, `Shape.kt`, `Motion.kt`) and reference it everywhere. Do not hardcode a hex value, sp size, dp value, or duration anywhere outside those theme files.

### 3.1 Color

Every pairing below has already been checked against WCAG AA (4.5:1 minimum for text, 3:1 minimum for icons and non-text UI components). Use these exact values rather than Material's baseline colors.

**Light scheme**

| Token | Hex | Usage |
|---|---|---|
| primary | `#2F6F5E` | FAB, filled buttons, active nav item, selected states |
| onPrimary | `#FFFFFF` | Text/icons on top of `primary` fills |
| primaryContainer | `#CFEEE1` | Selected chip/segment backgrounds, highlighted containers |
| onPrimaryContainer | `#0B3B2E` | Text/icons on `primaryContainer` |
| secondary | `#E8A33D` | Streak badge fills, warm accent chips — **fill use only**, see note |
| onSecondary | `#402D00` | Text/icons on `secondary` fills |
| secondaryContainer | `#FBE3C2` | Streak badge pill background |
| onSecondaryContainer | `#4A2E00` | Text on `secondaryContainer` |
| background | `#F1F3F0` | Screen background |
| onBackground | `#1C1E1B` | Primary text/icons on `background` |
| surface | `#FAFAF8` | Cards, sheets, app bar |
| onSurface | `#1C1E1B` | Primary text/icons on `surface` |
| surfaceVariant | `#E1E4DE` | Subtle chip fills, progress-ring tracks |
| onSurfaceVariant | `#45483F` | Secondary/caption text |
| outline | `#767A70` | Input borders, heatmap empty-cell outline |
| outlineVariant | `#C7CBC1` | Dividers (decorative only) |
| error | `#BA1A1A` | Destructive actions, validation |
| onError | `#FFFFFF` | Text/icons on `error` |
| errorContainer | `#FFDAD6` | Error banners/snackbars |
| onErrorContainer | `#410002` | Text on `errorContainer` |

> **Note on `secondary`:** raw `secondary` amber only reaches 2.06:1 against `surface`/`background` — enough for a filled badge paired with `onSecondary`, but not for small flat text or an icon glyph sitting directly on a light surface. For a streak flame/leaf glyph or number drawn straight onto `surface`/`background` **in light theme only**, use the dedicated token below instead.

| Token | Hex | Usage |
|---|---|---|
| streakAccentLight | `#8A5A00` | Small streak icon/number on `surface`/`background`, light theme only (5.3–5.7:1). In dark theme, `secondary` (`#F0C078`) already passes at 9.4:1 and can be used directly. |

**Dark scheme**

| Token | Hex | Usage |
|---|---|---|
| primary | `#8FD9C4` | FAB, filled buttons, active nav item, selected states |
| onPrimary | `#00382C` | Text/icons on `primary` fills |
| primaryContainer | `#1D4E40` | Selected chip/segment backgrounds |
| onPrimaryContainer | `#ABE8D5` | Text on `primaryContainer` |
| secondary | `#F0C078` | Streak badge fills, streak icon/number directly on surface |
| onSecondary | `#452C00` | Text/icons on `secondary` fills |
| secondaryContainer | `#5C3E00` | Streak badge pill background |
| onSecondaryContainer | `#FFDDAE` | Text on `secondaryContainer` |
| background | `#14181C` | Screen background |
| onBackground | `#E8ECE9` | Primary text/icons on `background` |
| surface | `#1E2422` | Cards, sheets, app bar |
| onSurface | `#E8ECE9` | Primary text/icons on `surface` |
| surfaceVariant | `#333B36` | Subtle chip fills, progress-ring tracks |
| onSurfaceVariant | `#C2C9BF` | Secondary/caption text |
| outline | `#8C9389` | Input borders, heatmap empty-cell outline |
| outlineVariant | `#43493F` | Dividers (decorative only) |
| error | `#FFB4AB` | Destructive actions, validation |
| onError | `#690005` | Text/icons on `error` |
| errorContainer | `#93000A` | Error banners/snackbars |
| onErrorContainer | `#FFDAD6` | Text on `errorContainer` |

**Heatmap-specific colors** (fixed brand colors, not theme-adaptive roles):

| Token | Hex | Usage |
|---|---|---|
| heatmapFillLight | `#2F6F5E` | Completed-day cell fill, light theme (same as brand `primary`) |
| heatmapFillDark | `#37876F` | Completed-day cell fill, dark theme — a brighter tone of the same brand teal. The exact light-theme teal only reaches 2.7:1 against the dark `surface` card; this variant reaches 3.7–4.1:1 while still reading as the same color family |
| Today-ring, on a filled cell | `#FFFFFF` | Both themes — 4.3–5.9:1 against either heatmap fill |
| Today-ring, on an empty cell | `onBackground` token | Both themes — 13–16:1 against either background |

### 3.2 Typography

Two bundled font families rather than the Roboto default — a warm geometric display face paired with a neutral, highly legible body face:

- **Display / headline / title role:** Manrope (weights 800/700/600) — rounded geometric letterforms, approachable without being twee.
- **Body / label role:** Inter (weights 400/500/600) — neutral and extremely legible at small sizes.
- Bundle both as local font files under `res/font/` at build time. Do **not** use the Downloadable Fonts / Google Fonts provider API — it performs a runtime fetch on first use, which conflicts with the zero-network-calls constraint in §9.

| Style | Face / weight | Size / line height | Used for |
|---|---|---|---|
| streakDisplay *(app-specific)* | Manrope ExtraBold | 40sp / 48sp | The big current-streak number, Habit Detail only |
| headlineSmall | Manrope Bold | 24sp / 32sp | Screen titles, onboarding headline |
| titleLarge | Manrope SemiBold | 20sp / 28sp | Section headers, dialog titles |
| titleMedium | Manrope SemiBold | 16sp / 24sp | Habit name in list rows and cards |
| bodyLarge | Inter Regular | 16sp / 24sp | Primary reading text, form input text |
| bodyMedium | Inter Regular | 14sp / 20sp | Secondary text, descriptions |
| labelLarge | Inter Medium | 14sp / 20sp | Buttons, tab labels |
| labelMedium | Inter Medium | 12sp / 16sp | Chips, streak badge number |
| labelSmall | Inter Medium | 11sp / 16sp | Captions, timestamps, helper/error text |

All sizes are `sp` so they scale with the system font setting. Never wrap text in a fixed-height container that would clip at 200% system scaling (§3.7).

### 3.3 Shape & Elevation

**Corner radius scale**

| Token | Radius | Used for |
|---|---|---|
| none | 0dp | — |
| xs | 4dp | Heatmap cells |
| sm | 8dp | Text fields, small buttons |
| md | 12dp | Cards — habit card, stat card, heatmap card |
| lg | 20dp | Bottom sheets, dialogs |
| full | 999dp (stadium) | FAB, filled buttons, chips, badges, segmented control |

**Elevation scale** (Material 3 tonal elevation, with a real shadow only at the higher levels)

| Level | dp | Used for |
|---|---|---|
| 0 | 0 | Screen background |
| 1 | 1 | Resting cards |
| 2 | 3 | App bar once content scrolls beneath it, bottom navigation bar |
| 3 | 6 | FAB resting |
| 4 | 8 | Dialogs, modal bottom sheets, FAB pressed |
| 5 | 12 | Reserved — not used in v1 |

### 3.4 Spacing

Base unit 4dp.

| Token | dp | Used for |
|---|---|---|
| micro | 4 | Icon-to-text gap inside a chip |
| small | 8 | Gap between list rows, gaps within a component |
| compact | 12 | Label-to-control gap |
| standard | 16 | Screen horizontal margins, card internal padding |
| section | 24 | Gap between form sections |
| large | 32 | Gap around major headers |
| generous | 48 | Empty-state breathing room, onboarding illustration-to-headline gap |

### 3.5 Iconography

Material Symbols via the `material-icons-extended` artifact — a compile-time library dependency, not a runtime fetch, so it doesn't conflict with §9. Outlined style by default; switch to the filled variant of the same glyph only to indicate a selected/active state (the active bottom-nav icon, the completed check circle). Standard glyph size 24dp; minimum touch target 48dp regardless of the visible glyph size. For the sprout/leaf motif used on the app icon and onboarding illustrations, draw a simple custom vector path rather than sourcing external art, so the app stays fully self-contained.

### 3.6 Motion

Durations: 100ms micro (ripples, switch toggles) · 200ms small (chip toggles, the specific-days row expanding) · 300ms standard (cross-fades, dialogs) · 400ms emphasized (the complete-habit "pop," push transitions). Easing: `FastOutSlowInEasing` for standard transitions, `LinearOutSlowInEasing` for elements entering, `FastOutLinearInEasing` for elements leaving.

Two signature moments carry all of the app's expressive motion — keep everything else restrained:

1. **Completing a habit (Home, §5.2):** the check circle scales 1.0 → 1.15 → 1.0 over ~400ms while its fill crossfades from an `outline` ring to a solid `primary` circle with a white check glyph; the streak number ticks up with a brief digit roll instead of an instant swap.
2. **Heatmap reveal (Habit Detail, §5.4):** cells fade and scale in with a ~6–8ms stagger per cell, oldest to newest, on first appearance only — never re-trigger this on an ordinary recomposition.

Respect the system's reduced-motion setting: check `Settings.Global.ANIMATOR_DURATION_SCALE` (0 when the user has turned off system animations) and swap both signature animations for an instant state change when it's 0. Every other transition — screen pushes, dialogs, sheets — should use plain, default Material 3 motion. Don't invent bespoke animation anywhere else in the app.

### 3.7 Accessibility Standards

- Every interactive element has a minimum 48×48dp touch target, even where the visible element is smaller.
- Every color pairing above is verified to WCAG AA. Where color is the only differentiator between two states (completed vs. not), pair it with a shape or icon change too — the heatmap already does this: filled square vs. outlined square, and a ring rather than a color marks "today."
- Every icon-only control and every heatmap cell needs a `contentDescription` — a heatmap cell should announce something like "March 3, completed," not render silently for TalkBack.
- Support system font scaling up to 200%: use `sp`, never clip text inside a fixed-height container, let cards and rows grow to fit.
- Support System/Light/Dark automatically (§5.6) in addition to a manual override.
- Respect reduced motion per §3.6.

### 3.8 Voice & Microcopy

Plain, active-voice labels: "Add Habit," "Save," "Delete Habit" — never "Submit" or "Confirm." A button's label and its resulting confirmation use the same verb: "Delete Habit" leads to a toast reading "Habit deleted," never "Removed successfully." Empty states are invitations, not error states — "No habits yet — add your first one," never "No data." Errors state what happened and how to fix it, without apologizing: "Give your habit a name," not "Oops, something went wrong." Use this voice consistently for every string specified in §5.

---

## 4. Data Model

**Habit**

| field | type | notes |
|---|---|---|
| id | Int (PK, autogenerate) | |
| name | String | required, 1–40 chars |
| emoji | String | single emoji, default "✅" |
| colorHex | String | one of 8 preset swatches, §5.3 |
| frequencyType | String | `"DAILY"` or `"SPECIFIC_DAYS"` |
| activeDays | String | comma-separated 1–7 (Sun=1..Sat=7); only used if SPECIFIC_DAYS |
| reminderEnabled | Boolean | default false |
| reminderHour | Int? | nullable |
| reminderMinute | Int? | nullable |
| createdAt | Long | epoch millis |
| sortOrder | Int | manual reordering |

**HabitCompletion**

| field | type | notes |
|---|---|---|
| id | Int (PK, autogenerate) | |
| habitId | Int (FK → Habit.id, cascade delete) | |
| date | String | ISO `"yyyy-MM-dd"` |
| completedAt | Long | epoch millis |

A habit can have at most one completion per date — enforce that with a unique composite index rather than relying on application logic alone.

```kotlin
@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val emoji: String = "✅",
    val colorHex: String,
    val frequencyType: String, // "DAILY" or "SPECIFIC_DAYS"
    val activeDays: String = "", // e.g. "1,3,5"
    val reminderEnabled: Boolean = false,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val sortOrder: Int = 0
)

@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["habitId"]),
        Index(value = ["habitId", "date"], unique = true)
    ]
)
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String, // "yyyy-MM-dd"
    val completedAt: Long = System.currentTimeMillis()
)
```

**DAOs**

```kotlin
@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("SELECT * FROM habits ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun observeById(habitId: Int): Flow<Habit?>
}

@Dao
interface HabitCompletionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteForHabitAndDate(habitId: Int, date: String)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY date ASC")
    fun observeForHabit(habitId: Int): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE date = :date")
    fun observeForDate(date: String): Flow<List<HabitCompletion>>

    @Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND date = :date)")
    fun observeIsCompleted(habitId: Int, date: String): Flow<Boolean>
}
```

Because `HabitCompletion` cascades on delete, removing a `Habit` automatically removes its completion history — no separate cleanup method is needed.

**Streak rule:** current streak = consecutive *scheduled* days completed, counting backward from today (or from yesterday if today is scheduled but not yet marked). Best streak = longest historical run. For `SPECIFIC_DAYS` habits, unscheduled days are skipped entirely — they neither extend nor break a streak. Implement this as a standalone object with an injectable clock, so it's unit-testable without touching Room or the system clock:

```kotlin
object StreakCalculator {
    fun calculate(
        habit: Habit,
        completionDates: Set<String>, // ISO "yyyy-MM-dd"
        today: LocalDate = LocalDate.now()
    ): StreakResult
}

data class StreakResult(val current: Int, val best: Int)
```

Required unit-test cases: no completions at all (0/0); an unbroken daily streak ending today; a daily habit completed through yesterday but not yet today (current streak still counts through yesterday); a daily habit with a gap in the middle (best reflects the longest run, current reflects only the most recent run); a specific-days habit where a non-scheduled day sits between two completed scheduled days (streak unbroken); a specific-days habit where a scheduled day was missed (streak breaks exactly there).

**App preferences** (DataStore, not Room) — two lightweight settings that don't belong in a relational table:

| key | type | default | notes |
|---|---|---|---|
| `has_completed_onboarding` | Boolean | `false` | flips permanently true once §5.1 finishes |
| `theme_mode` | String | `"SYSTEM"` | one of `"SYSTEM"` / `"LIGHT"` / `"DARK"`, §5.6 |

Wrap a single `Context.dataStore` delegate in a small `PreferencesRepository`, exposed the same way `HabitRepository` exposes Room — `Flow` for reads, `suspend fun` for writes.

---

## 5. Screens & UI/UX Specification

Build every screen using only the tokens defined in §3 — no ad-hoc colors, sizes, or durations.

### 5.1 Onboarding

First launch only, gated by `has_completed_onboarding` (§4). Two full-bleed pages in a horizontal pager:

- **Layout:** centered custom vector illustration (~120dp, §3.5) → `generous` gap → `headlineSmall` headline → `bodyLarge` supporting line → two page-indicator dots (`primary`-filled 8dp when active, `outlineVariant` 8dp when inactive) → a full-width filled button pinned to the bottom (`standard` margins, `full` shape, 48dp height).
- **Slide 1:** headline "Small steps, every day"; body "Habitly helps you build routines that stick — no accounts, no tracking, just you and your habits."; button reads "Next"; sprout illustration.
- **Slide 2:** headline "Your data stays on your phone"; body "Everything is stored locally. Nothing is ever uploaded or shared."; button reads "Get Started"; simple phone/lock illustration.
- **Interaction:** swipeable, but the button always advances too, so a user never has to discover the swipe gesture. Tapping "Get Started" sets `has_completed_onboarding = true` and navigates to Home with `popUpTo("onboarding") { inclusive = true }` (§6), so the back gesture from Home can never return here.
- **Accessibility:** announce "Page 1 of 2" / "Page 2 of 2" via semantics — the dot indicator alone isn't accessible.

### 5.2 Home / Today

The app's default screen, and the one a returning user sees most.

- **Top bar:** small top app bar, title "Habitly," no navigation icon — this is the root destination.
- **Date row:** a quiet `bodyMedium`/`onSurfaceVariant` line under the app bar showing today's date, e.g. "Monday, July 6."
- **List:** a `LazyColumn`, `standard` horizontal margins, `small` gaps between rows. Each row is a `HabitCard` — `md` shape, `surface` fill, elevation level 1, `standard` internal padding, laid out as:
  - a 40dp circular chip filled with the habit's `colorHex` at ~15% alpha, containing the habit's emoji
  - a column with the name (`titleMedium`) and the streak (`labelMedium`, a small flame/leaf glyph plus number, colored per the streak-accent rule in §3.1)
  - a trailing 32dp check-tap circle inside a 48dp touch target: an `outline`-colored ring when incomplete; a solid `primary` fill with a white check glyph when complete, animated per §3.6
- **Ordering:** rows stay in the habit's manual `sortOrder` position and never reorder on tap — only their fill and opacity change. Reordering on tap is disorienting and risks a mis-tap on a target that just moved. Completed rows dim to ~60% opacity to signal "done" without leaving the list.
- **FAB:** extended FAB, bottom-end, "Add Habit" plus a plus icon, `full` shape, elevation level 3 → navigates to Add/Edit Habit (§5.3) in create mode.
- **Empty state, no habits at all:** centered sprout illustration (~160dp) + `headlineSmall` "No habits yet" + `bodyLarge` "Add your first one to start building a streak." The FAB is the only call to action here — don't duplicate it with an inline button.
- **Empty state, habits exist but none scheduled today:** a quieter, lower-emphasis message only — "Nothing scheduled for today — enjoy the day off." No illustration; this is a neutral state, not one that needs encouragement to act.
- **Motion:** the complete/incomplete "pop" from §3.6; on first entry only, rows fade/slide in with a light stagger (~30ms) — never replay this on a completion toggle.
- **Accessibility:** each check-tap target's `contentDescription` updates dynamically — "Mark {habit name} complete for today" / "Mark {habit name} incomplete."

### 5.3 Add / Edit Habit

- **Top bar:** small top app bar, back arrow, title "Add Habit" or "Edit Habit."
- **Form** (scrollable column, `standard` margins, `section` gaps between groups):
  1. **Name** — outlined text field, `bodyLarge`, live 1–40 character counter in `labelSmall` once focused; inline error "Give your habit a name" in `error` if left empty on submit.
  2. **Icon** — label "Icon"; a 56dp circular preview of the current selection; tapping opens a modal bottom sheet with a 5-column grid of 20 presets: 🌱 💧 📚 🏃 🧘 😴 🍎 💪 🎨 🎯 ☀️ 🧹 💰 ✍️ 🎵 🚭 🥗 🧠 🙏 ⭐. Tapping one closes the sheet.
  3. **Color** — label "Color"; a row of 8 swatches (32dp circles, small gaps): forest teal `#2F6F5E`, amber `#E8A33D`, moss green `#6B8E4E`, sky blue `#4A7FA6`, terracotta `#C4653A`, plum `#8E4A6B`, slate `#5B6470`, indigo `#5C5EA6`. The selected swatch gets a 2dp `onSurface` ring offset outside it — a shape change, not just a color change, so it still reads for colorblind users.
  4. **Frequency** — label "Frequency"; a two-segment button, "Every day" / "Specific days." Choosing "Specific days" reveals seven single-letter weekday chips (S M T W T F S) below via a 200ms expand+fade; each toggles independently, filled with `primaryContainer`/`onPrimaryContainer` when active.
  5. **Reminder** — label "Reminder"; a switch row. Enabling it reveals a "Time" row showing the chosen time in `bodyLarge` with a trailing edit icon that opens the Material 3 time picker.
  6. **Actions** — full-width filled "Save" button (`primary`, `full` shape). In edit mode only, an additional text-style "Delete Habit" button in `error`, which opens a confirmation dialog: "Delete {habit name}? This also removes its full history. This can't be undone." / "Cancel" / "Delete."
- **States:** create mode hides the delete button entirely; edit mode pre-fills every field from the existing `Habit`.
- **Motion:** only the weekday-chip reveal animates (§3.6's small tier). Everything else in a form should feel calm and instant, not decorated.
- **Accessibility:** every emoji and swatch needs a `contentDescription` ("Amber," "Sprout emoji," etc.); the error text is associated with the name field so TalkBack announces it, not just displays it visually.

### 5.4 Habit Detail

- **Top bar:** small top app bar, title = habit name, trailing overflow (⋮) menu with "Edit" and "Delete."
- **Header:** 64dp emoji chip, name (`headlineSmall`), then two stat chips side by side inside `surfaceVariant`-tinted `md`-shaped containers: "Current streak" (label, `streakDisplay` number, flame/leaf glyph) and "Best streak" (same treatment).
- **Heatmap card** (the signature element — §3.1, §3.6): `surface` fill, `md` shape, `standard` padding. A small "Last 12 weeks" label, then a grid of **12 columns (weeks, oldest→newest, left to right) × 7 rows (Sunday→Saturday, top to bottom)** — 84 cells total, matching the Sun=1..Sat=7 convention already used for `activeDays` (§4). Cell size ~14dp, `micro` gaps, `xs` corner radius per cell.
  - Completed day: solid fill (`heatmapFillLight`/`heatmapFillDark`, §3.1)
  - Empty day: 1dp `outline`-colored stroke only, no fill
  - Future date (hasn't occurred yet): same outline style at ~38% opacity
  - Today: an additional 2dp ring — white if the cell is filled, `onBackground` if it's empty
  - A two-item legend below the grid: an outline swatch labeled "Less," a filled swatch labeled "More," both `labelSmall`/`onSurfaceVariant`
  - If the habit has zero completions yet, render the full grid as all-outline cells rather than hiding it, with a quiet caption underneath: "Mark today complete on Home to start your streak."
- **Motion:** the staggered cell reveal from §3.6, first appearance only. The delete confirmation dialog uses the exact same copy as §5.3's, word for word — the action means the same thing in both places.
- **Accessibility:** every cell needs its own `contentDescription` stating the literal date and completed/not-completed state (e.g., "March 3, completed") — this is the one component in the app that's otherwise pure color and shape, so TalkBack support here matters more than anywhere else.

### 5.5 Stats

- **Top bar:** small top app bar, title "Stats."
- **Summary row:** two side-by-side `surface` cards, each with a circular progress ring (Compose `Canvas`/`Arc`, 8dp stroke, `surfaceVariant` track, `primary` progress arc), the percentage centered in the ring using `streakDisplay`-scale numerals, labeled "This week" / "This month" below.
- **By-habit list:** a "By Habit" `titleLarge` section header, then one row per habit — emoji chip + name (`titleMedium`) + a horizontal bar (`surfaceVariant` track, filled with **that habit's own `colorHex`**, tying it back to the color chosen in §5.3) + a trailing percentage (`labelLarge`).
- **Empty state:** "Nothing to show yet" + "Complete a few habits and your stats will show up here," used whenever there are no habits or no completions recorded.
- **Motion:** rings and bars sweep from 0 to their real value once, on screen entry (~500ms, `FastOutSlowInEasing`) — a single restrained moment, not a loop.
- **Accessibility:** every ring and bar needs a `contentDescription` stating the literal percentage as text — a sighted-only arc conveys nothing to TalkBack otherwise.

### 5.6 Settings

- **Top bar:** small top app bar, title "Settings."
- **Appearance section:** a row labeled "Theme" with an inline three-option segmented control (System / Light / Dark) for immediate feedback with no extra taps; writes straight to `theme_mode` (§4).
- **Notifications section:** a row showing the live permission state — "Allowed" in `onSurfaceVariant`, "Not allowed" in `error`. If not allowed, a trailing text button "Turn on" opens the system notification-settings page for the app (`Settings.ACTION_APP_NOTIFICATION_SETTINGS`) — Android doesn't allow re-prompting a denied runtime permission directly, so this is the correct path, not a dead end.
- **About section:** app icon (32dp) + "Habitly" + version string from `BuildConfig.VERSION_NAME`, plus a one-line reminder of the offline promise: "Runs fully offline. No account, no tracking." — echoing onboarding slide 2 so the message is consistent everywhere it appears.
- **Motion:** none beyond default list/dialog behavior — Settings should be the quietest screen in the app.
- **Accessibility:** the segmented control exposes each option's selected state to TalkBack, not just a visual highlight; the notification row's `contentDescription` states the concrete permission state in words.

---

## 6. Navigation & Transitions

```
onboarding → home ⇄ { add_edit/{habitId?}, habit_detail/{habitId}, stats, settings }
```

Bottom navigation with three peer destinations — Home, Stats, Settings. Add/Edit and Detail are pushed on top of whichever bottom-nav tab launched them, with the bottom nav hidden while they're visible.

- **Pushed screens** (Add/Edit, Detail) slide in from the trailing edge while the screen behind slides partially out — Material 3's standard shared-axis-x pattern. Use the platform/library default rather than a custom transition.
- **Bottom-nav switches** cross-fade only; they're peers, not a hierarchy, so a directional slide would be misleading.
- **Back gesture:** rely on the platform's predictive-back and the NavHost's default back-stack handling — don't override it with custom behavior.
- **Onboarding exit:** on "Get Started," pop onboarding out of the back stack entirely (`popUpTo("onboarding") { inclusive = true }`) so it can never be reached via system back from Home (§5.1).

Given targetSdk will enforce edge-to-edge by default, confirm the bottom navigation bar and every top app bar apply the correct system-bar insets so nothing sits underneath the status bar or gesture-nav bar on any screen.

---

## 7. Build Phases

Work through these strictly in order, one phase per turn, per §1. The order is deliberate: plan first, then the design tokens alone, then a navigable skeleton, then the complete visual frontend running on placeholder data, and only then real functionality — one screen or concern at a time — finishing with reminders and a dedicated final polish pass. Each phase is narrow on purpose; do not fold two of them into one turn.

**Phase 0.1 — Goals, Plan & Blueprint**
No code yet. Read this entire specification and write a `PLAN.md` at the project root containing: a one-paragraph restatement of what Habitly is and its core promise (local-only, no accounts, no tracking); the full phase list below with a one-line description of each; and the key decisions this spec already fixes (package `com.habitly.app`, minSdk 26, MVVM architecture, the rest of §2). Flag anything genuinely ambiguous and how you'll resolve it per §1's rule before moving on.
✅ Done when: `PLAN.md` exists, correctly lists every phase in order, and leaves no open question you haven't already resolved yourself.

**Phase 0.2 — Layout Style & Color Design**
Build the design tokens from §3 as real files — `Color.kt` (light and dark schemes in full), `Type.kt` (with the Manrope and Inter font files bundled under `res/font`), `Shape.kt`, and `Motion.kt` — and nothing else yet. Render them on one temporary preview screen (set as `MainActivity`'s only content for now) showing every color swatch with its name, a sample line in every type style, and the shape scale, in both light and dark.
✅ Done when: the temporary preview screen compiles, runs, and visibly matches §3.1–§3.4 in both themes — nothing else about the app exists yet.

**Phase 1 — Basic App Skeleton**
Replace the Phase 0.2 preview with the real app shell: `NavHost` wired for every route in §6, bottom navigation across the Home/Stats/Settings peers, and back-stack navigation for the pushed Add/Edit and Detail routes. Every screen is just a correctly themed, correctly titled app bar over a blank body — no real layout content yet.
✅ Done when: you can navigate to every screen and back, the right title shows on each app bar, and the bottom-nav and push transitions from §6 both work, with nothing else on any screen yet.

**Phase 2 — Frontend for Every Screen**
Build the complete visual layout for all six screens plus onboarding, exactly as specified in §5 — every component, spacing value, and shape from §3, on every screen. Populate it from a temporary `data/SampleData.kt`: 4–5 sample habits with varied emoji, colors, and frequencies (include at least one `SPECIFIC_DAYS` habit), fake streak numbers, and a scattering of fake completed/incomplete heatmap cells. Interactive elements can no-op or update local Compose state only — nothing needs to persist or be functionally correct yet.
✅ Done when: every screen visually matches §5 in both light and dark themes using the sample data, and the whole app can be clicked through screen to screen looking completely finished, even though none of it is wired to real data yet.

**Phase 3 — Data Layer**
The `Habit` and `HabitCompletion` entities exactly as specified in §4 (including the foreign key and both indices), both DAOs, `AppDatabase`, `HabitRepository`, the DataStore-backed `PreferencesRepository` for `has_completed_onboarding` and `theme_mode`, and the `StreakCalculator` object with its full unit-test suite (all six required cases). None of this is wired to any screen yet.
✅ Done when: a dummy habit can be inserted and read back via a temporary debug action and survives an app restart, a preference value survives a restart, and every `StreakCalculator` unit test passes.

**Phase 4 — Home Screen Functionality**
Replace Phase 2's sample data on Home with `HomeViewModel`'s real `StateFlow`, sourced from the Phase 3 repository. Wire the check-tap circle to actually insert or delete today's `HabitCompletion` and update the streak badge live, with the §3.6 completion animation.
✅ Done when: Home shows real habits in their real sort order, marking/unmarking today updates the real streak number, and rows never reorder on tap.

**Phase 5 — Add / Edit Habit Functionality**
Wire Phase 2's Add/Edit form to `HabitRepository`'s real create, update, and delete, including the name validation and the delete confirmation dialog.
✅ Done when: create, edit, and delete all reflect immediately back on the real Home list, and an empty name is actually blocked rather than just visually flagged.

**Phase 6 — Habit Detail & Streak Functionality**
Replace Phase 2's fake heatmap and stat chips with real data from the repository, run through the already-tested `StreakCalculator` from Phase 3.
✅ Done when: opening any real habit shows an accurate 12-week grid built from its real completion history (§3.1 for cell styling), the today-ring is in the right place, and the current/best streak numbers match what `StreakCalculator` actually returns for that habit.

**Phase 7 — Stats Functionality**
Replace Phase 2's fake Stats numbers with a real aggregate weekly/monthly completion percentage across all habits, and real per-habit comparison rows.
✅ Done when: every number on Stats updates correctly as completions change elsewhere in the app, and each habit's bar still uses that habit's own color.

**Phase 8 — Settings & Theme Functionality**
Wire the theme segmented control already built in Phase 2 to the real `theme_mode` preference, applied app-wide.
✅ Done when: changing the theme control updates every screen instantly and the choice survives an app restart.

**Phase 9 — Reminders & Notifications Functionality**
`POST_NOTIFICATIONS` handling for Android 13+, and real local reminder scheduling per habit using inexact scheduling (`setAndAllowWhileIdle` or `WorkManager` — a reminder landing within a couple of minutes of the set time is fine for a habit app, and it avoids the heavier `SCHEDULE_EXACT_ALARM` permission), wired to the reminder switch and time picker already built in Phase 2's Add/Edit screen, plus a real permission-state readout on Settings' notification row.
✅ Done when: a habit with a reminder enabled actually fires a local notification at roughly the right time, and the Settings notification row reflects the real permission state rather than a placeholder.

**Phase 10 — Onboarding Functionality**
Wire Phase 2's onboarding slides to the real `has_completed_onboarding` preference: show them only on first launch, and navigate to Home with `popUpTo("onboarding") { inclusive = true }` on "Get Started."
✅ Done when: onboarding appears once on a fresh install, never appears again after "Get Started," and the back gesture from Home never returns to it.

**Phase 11 — Final Polish & Definition of Done**
Delete `data/SampleData.kt` and confirm nothing still references it. Sweep every empty state from §5 and every input-validation edge case, and add the adaptive app icon and splash screen built from the sprout motif (§3.5). Full click-through: rotate the device, kill and reopen the app, background and foreground it — nothing should crash. Verify every item in §9's Definition of Done, including 200% font scale and system animations turned off (§3.7).
✅ Done when: a stranger could use the whole real app with no confusion and no crash, in both themes, at large font scale, and with animations disabled — and every item in §9 checks out.

---

## 8. Target File Structure

```
app/src/main/java/com/habitly/app/
├── data/
│   ├── Habit.kt
│   ├── HabitCompletion.kt
│   ├── HabitDao.kt
│   ├── HabitCompletionDao.kt
│   ├── AppDatabase.kt
│   ├── HabitRepository.kt
│   ├── PreferencesRepository.kt     DataStore wrapper: has_completed_onboarding, theme_mode
│   └── StreakCalculator.kt
├── ui/
│   ├── onboarding/
│   ├── home/
│   ├── addedit/
│   ├── detail/
│   ├── stats/
│   ├── settings/
│   ├── components/
│   │   ├── HabitCard.kt
│   │   ├── StreakBadge.kt
│   │   ├── EmojiPicker.kt
│   │   ├── ColorSwatchPicker.kt
│   │   ├── WeekdayChips.kt
│   │   ├── HeatmapGrid.kt
│   │   ├── ProgressRing.kt
│   │   ├── EmptyState.kt             reusable illustration + headline + body
│   │   └── ConfirmDialog.kt          reusable, used by both delete flows (§5.3, §5.4)
│   └── theme/
│       ├── Color.kt
│       ├── Type.kt
│       ├── Shape.kt
│       ├── Motion.kt                 duration + easing constants from §3.6
│       └── Theme.kt
├── notifications/
│   ├── ReminderScheduler.kt
│   └── ReminderReceiver.kt
├── navigation/
│   └── NavGraph.kt
└── MainActivity.kt

res/font/                              bundled Manrope + Inter font files (§3.2)
```

---

## 9. Hard Constraints & Definition of Done

**Hard constraints — apply to every phase, no exceptions:**
- No backend, no network calls of any kind, no login/auth
- No ads SDK, no analytics/crash-reporting SDK
- No runtime font-fetching — Manrope and Inter are bundled locally (§3.2)
- No permissions beyond notifications, and only if reminders are actually used
- No third-party dependency beyond Jetpack/AndroidX unless something genuinely required is missing from it
- Idiomatic, commented Kotlin; small, single-purpose composables over giant ones

**Definition of done — the whole app, not just one phase:**
- Builds and runs with zero crashes through the full Phase 11 click-through
- Every screen uses only the tokens from §3 — no hardcoded colors, sizes, or durations outside the theme files
- Both light and dark themes verified on every screen
- Every interactive element meets the 48dp minimum touch target
- TalkBack can reach and meaningfully announce every screen, including every heatmap cell
- Text holds up at 200% system font scale without clipping or truncation anywhere
- Turning off system animations (§3.6) disables both signature motion moments without breaking any functionality
