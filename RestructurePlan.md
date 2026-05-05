# Newscroll — Restructure Plan
**Created:** 2026-05-05  
**Context:** AI-assisted session (Claude Code) made 17 commits after the last stable team commit, breaking compilation for teammates and destroying working features (saved articles, messages, auth). This document is the recovery and forward plan.

---

## What Went Wrong — Root Cause Analysis

### Problem 1: Build tool versions jumped to incompatible releases
The session bumped AGP from `8.13.2` → `9.2.0` and Kotlin from `2.0.21` → `2.2.10`. Teammates' Android Studio versions cannot compile these. Room was also moved to an unstable alpha (`2.7.0-alpha11`).

### Problem 2: `UserViewModel` was deleted and split
The original monolithic `UserViewModel` (handling auth + profile + saves + social) was replaced with 4 separate ViewModels (`AuthViewModel`, `ProfileViewModel`, `SavedArticlesViewModel`, `SocialViewModel`). Every screen that referenced `UserViewModel` broke.

### Problem 3: `NewsRepository` was fully replaced
The original `NewsRepository` was a simple Kotlin `object` with a static 3-article list (scaffolding for future API work). It was replaced with a complex `class` calling live APIs, requiring `BuildConfig` keys from `local.properties`. Any teammate missing that file cannot compile.

### Problem 4: `NewsArticle` model fields changed
`category` changed from `String?` (nullable) to `String` (non-nullable), and new fields (`summary`, `publishedDate`, `source`) were added — breaking existing entity mappings and hardcoded articles.

---

## The Safe Commit

```
dd75e01fb91dc17f121b79aa30ff008660d40c6a
```

**Author:** srmadrid (teammate, not AI session)  
**Date:** Saturday May 2, 2026 — the last commit before this session started  
**What it contains:**
- AGP `8.13.2`, Kotlin `2.0.21`, Room `2.6.1`, KSP `2.0.21-1.0.28` — all stable
- `UserViewModel` intact (monolithic: auth + profile + saves + social)
- Working: feed (3 hardcoded articles), saved, messages/chat, auth, share, settings
- All API service interfaces scaffolded in `data/remote/` but not yet called
- Room database scaffolded but not yet called
- Unit and instrumented tests added by teammate

**Why this commit and not an earlier one:** it is the most recent state the full team validated and compiled successfully. It also includes the tests, which confirm the architecture was stable.

---

## Architecture to Preserve (Do Not Change)

These patterns were working and must be kept exactly as-is:

| Item | Rule |
|---|---|
| `UserViewModel` | Keep monolithic — one ViewModel for auth, profile, saves, and social |
| `build.gradle.kts` versions | AGP 8.13.2, Kotlin 2.0.21, Room 2.6.1, KSP 2.0.21-1.0.28 |
| `gradle/libs.versions.toml` | Restore from dd75e01 — no version bumps |
| Navigation routes | `feed`, `saved`, `settings`, `profile`, `messages`, `add_friend`, `chat/{chatId}` |
| Firebase Firestore saves | Article IDs saved to `users/{uid}/saved_articles/{articleId}` |
| `NewsAppShell` / `NavHost` wiring | Restore the clean 3-ViewModel wiring from dd75e01 |
| `ShareBottomSheet` | Keep avatar-based friend-sharing UI from original |
| `SavedScreen` | Keep the richer `SavedItem`-based UI from original |

---

## Phase 1 — Revert to Safe Base
**Goal:** Every teammate can compile and run again  
**Estimated time:** 30 minutes  
**Files changed:** entire repo (restore to dd75e01 state)

### Commands
```bash
cd /home/admin/StudioProjects/Newscroll
git checkout dd75e01fb91dc17f121b79aa30ff008660d40c6a -- .
git commit -m "revert: restore to last stable team commit dd75e01"
git push
```

### Verification checklist
- [ ] `./gradlew assembleDebug` succeeds with no errors
- [ ] Teammates can compile from a clean clone
- [ ] Feed shows 3 hardcoded articles and scrolls vertically
- [ ] Saved tab shows saved article titles
- [ ] Messages screen opens and chat works
- [ ] Login / register / logout works
- [ ] Settings screen shows category chips and location slider
- [ ] Share bottom sheet shows friend avatars

---

## Phase 2 — Add Real News Fetching
**Goal:** Replace the 3 hardcoded articles with real API content  
**Estimated time:** 2–3 hours  
**Files to modify:** `NewsRepository.kt`, `NewsViewModel.kt`  
**Files NOT to touch:** `UserViewModel.kt`, all screen files, build files

### Approach
Enhance `NewsRepository` by adding a `suspend fun fetchFromApis(categories: Set<String>)` method. Keep the original static `articles` list as the fallback. Do NOT change the repository from `object` to `class` — keep it as a Kotlin `object`.

The API service interfaces already exist in `data/remote/` (HackerNewsApiService, GuardianApiService, FinnhubApiService, RssFeedParser). They just need to be called.

### API key handling
API keys go in `local.properties` (never committed — already in `.gitignore`). Each teammate adds their own. **This file must not be committed.** Keys needed:
- `GUARDIAN_API_KEY` — free at open-platform.theguardian.com
- `FINNHUB_API_KEY` — free at finnhub.io

For teammates who don't have keys yet, the code must gracefully skip those sources (try/catch per source, return empty list on failure).

### Firestore article storage
After fetching from APIs, store each article as a Firestore document:
```
articles/{category}/{articleId}  →  { title, description, imageUrl, articleUrl, source, publishedDate }
```
This is the key architectural decision from the team: saved article IDs need to point to something. Storing articles in Firestore means a saved ID can always resolve to its full content, and articles can be shared in chats with real data attached.

### Commits for this phase (one per source)
1. `feat(data): fetch HackerNews articles and store in Firestore`
2. `feat(data): fetch Finnhub finance articles and store in Firestore`
3. `feat(data): fetch Guardian politics articles and store in Firestore`
4. `feat(data): fetch Survival + History RSS feeds and store in Firestore`

Each commit must pass `./gradlew assembleDebug` before the next one starts.

---

## Phase 3 — Fix Saved Articles Tab
**Goal:** Saved tab shows actual article content (not empty cards)  
**Estimated time:** 1 hour  
**Files to modify:** `SavedScreen.kt`, `UserViewModel.kt` (save method only)

### Problem
Currently, saving an article stores its ID in Firestore (`users/{uid}/saved_articles/{articleId}`). But those IDs point to nothing — the articles only existed in memory. After Phase 2, articles exist in Firestore under `articles/{category}/{articleId}`.

### Fix
Update `UserViewModel.getSavedArticles()` to:
1. Fetch saved IDs from `users/{uid}/saved_articles/`
2. For each ID, read the full article from `articles/{category}/{articleId}` in Firestore
3. Return a list of full `NewsArticle` objects to `SavedScreen`

No architecture changes — same ViewModel, same screen, same Firebase structure. Just wire the ID resolution.

---

## Phase 4 — UI Improvements
**Goal:** Add the visual improvements from this session without touching architecture  
**Estimated time:** 2 hours  
**Files to modify:** `NewsAppShell.kt`, `NewsFeedScreen.kt`

These changes are purely additive to the UI shell. Each is one file, one commit, verified after applying.

### 4a — Drawer hero banner (`NewsAppShell.kt`)
Add a gradient banner at the top of the `ModalDrawerSheet` showing:
- Circular avatar (user photo from Firebase Storage, or person icon placeholder)
- Username from `UserViewModel.displayName`
- Tagline: "Newscroll Member" / "Sign in to personalize"

### 4b — Icons on drawer navigation items (`NewsAppShell.kt`)
Add icons to all `NavigationDrawerItem` entries:
- Feed → `Icons.Default.Home`
- Saved → `Icons.Default.Bookmark`
- Profile → `Icons.Default.Person`
- Messages → `Icons.Default.Forum`
- Logout/Login → `Icons.Default.Logout` / `Icons.Default.Login`

### 4c — Remove Settings from drawer, add controls to TopAppBar (`NewsAppShell.kt`)
Remove the Settings navigation item from the drawer. Add 3 icon buttons to the TopAppBar actions (only visible on the feed screen):
- **Theme toggle** (sun/moon/auto icon) — cycles SYSTEM → LIGHT → DARK → SYSTEM
- **Location mode toggle** (pin/map/globe icon) — cycles LOCAL → NATIONAL → GLOBAL
- **Category filter** (filter icon) — opens `CategoryFilterSheet` bottom sheet

Both use the existing `SettingsViewModel` — no ViewModel changes needed.

### 4d — Category filter bottom sheet (`NewsFeedScreen.kt`)
Add `CategoryFilterSheet` composable: shown when the filter icon is tapped, displays All chip + individual category chips, calls `settingsViewModel.toggleFilter()` / `settingsViewModel.selectAll()`. Closing the sheet triggers re-fetch via the `LaunchedEffect(selectedFilters)` already in the shell.

---

## Phase 5 — Final Testing and Submission
**Goal:** Everything works end-to-end, ready for professor demo  
**Estimated time:** 2 hours

### Professor requirements coverage check
| Requirement group | Implementation | Status |
|---|---|---|
| Persistent storage | SharedPreferences (settings) + Room (article cache) + Firestore (saves + articles) | ✅ |
| Maps / location | GPS in `NewsViewModel` + Local/National/Global mode filter | ✅ |
| REST services | HackerNews API + Finnhub API + Guardian API + RSS feeds | ✅ |
| Background services | `FetchNewsWorker` via WorkManager (already in repo at dd75e01) | ✅ |

### End-to-end test checklist
- [ ] Fresh install (uninstall app from emulator first, then `./gradlew installDebug`)
- [ ] Feed loads real articles from APIs
- [ ] Category filter changes the articles shown
- [ ] Save an article → appears in Saved tab with full content
- [ ] Login, open messages, send a message, share an article to a friend
- [ ] Theme toggle switches Light/Dark/System
- [ ] Location mode toggle cycles through modes
- [ ] Logout and back in — saved articles persist

### Submission
```bash
git push
```
Then: record YouTube demo, zip source code, write max 10-page report referencing this plan.

---

## Known Issues for Future Iterations (Not Blocking Submission)

| Issue | Priority | Notes |
|---|---|---|
| History & Philosophy RSS feeds unreachable | Medium | Stanford SEP and History Today may be blocked from emulator — need fallback sources |
| Photo upload not working | Medium | Firebase Storage upload in `UserViewModel.uploadProfileImage()` needs debugging |
| Re-register allows username re-change | Low | Add a guard: if `displayName` is already set in Firestore, skip the rename prompt |
| Some articles missing or unrelated images | Low | Unsplash `source.unsplash.com` deprecated — replace with direct keyword image API |
| Spain / Europe regional content | Low | Add country-specific news APIs (e.g. El País RSS, Europa Press) |
| 5 world regions selector | Low | Replace location mode with region picker (Europe, Americas, Asia, Africa, Oceania) |

---

## File Change Log

| Phase | File | Action |
|---|---|---|
| 1 | All files | Restore to dd75e01 state |
| 2 | `data/NewsRepository.kt` | Add `fetchFromApis()` + Firestore storage |
| 2 | `viewmodel/NewsViewModel.kt` | Call `fetchFromApis()` on launch and filter change |
| 3 | `viewmodel/UserViewModel.kt` | Update `getSavedArticles()` to resolve IDs from Firestore |
| 3 | `ui/screens/SavedScreen.kt` | Display full resolved articles |
| 4 | `ui/screens/NewsAppShell.kt` | Hero banner + icons + TopAppBar controls |
| 4 | `ui/screens/NewsFeedScreen.kt` | Category filter bottom sheet |
| 5 | — | Testing only, no code changes |
