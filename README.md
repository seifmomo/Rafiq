# RAFIQ — Your Companion, Every Step

**RAFIQ** ("Rafeeq" — Arabic for *companion/friend*) is an AI-powered accessibility companion for people with disabilities: real-time guidance, emergency SOS, AI chat assistance, navigation, learning tools, and gamified rewards. Built with an Android (Kotlin + Jetpack Compose) app and a Node.js/Express/PostgreSQL backend.

> 🏆 **Competition / demo material is in [`docs/COMPETITION_PITCH.md`](docs/COMPETITION_PITCH.md)** — presentation prompt, slide-by-slide script, demo checklist, and judge Q&A.

## Repo Layout

```
rafiq/
├── app/          Android app (Kotlin, Jetpack Compose, Hilt, Room)
├── backend/      REST + WebSocket API (Express, PostgreSQL, JWT)
└── gradle/       Gradle wrapper config
```

## Features

| | Feature | What it does |
|---|---|---|
| 🆘 | **Emergency SOS** | 10s countdown → alert contact by SMS/Firebase; always-available fallbacks: **Share SOS alert**, **Open location in Google Maps**, **Call emergency number** |
| 🤖 | **AI Chat Assistant** | Conversation with memory, one-tap clear chat (instant, cloud deletes run in background), AI replies via any **OpenAI-compatible** provider + offline accessibility fallback |
| 🗺️ | **Real Map & Places** | **OpenStreetMap** of Cairo with wheelchair/sign-language/braille markers (+50 pts); auto-seeded demo places; every place has an **Open in Google Maps** deep link |
| 🎙️ | **Voice Assistant** | Speech-to-text with avatar; replies read aloud via TTS and saved to chat history |
| ✋ | **Sign Language** | CameraX + MediaPipe: **10 signs** (Fist, Hello, A, Yes, No, Peace, I Love You, OK, Rock, L) with live TTS |
| 🏆 | **Companion Score** | Gamified points, levels, leaderboard |
| 👥 | **Contacts** | Emergency contacts used by SOS + guardian mode |
| 💊 | **Medication reminders** | Track and get reminded to take medication |
| 🏥 | **Hospital finder** | Find nearby accessible care |
| 📚 | **Learning center** | Real ASL + mobility videos, awareness & rights |
| 👁️ | **Be My Eyes** | Simulated live volunteer camera (demo) |
| ♿ | **Accessibility** | AR/FR localization, dark/light/system themes, font size & family scaling, TTS speed |
| 🔐 | **Auth & backup** | Guest mode, JWT auth, backup/restore |
| ✅ | **Automated tests** | AI reply engine + gesture classifier unit tests + Room DAO tests |

### 🔍 How to use (emoji legend)

| Screen | Step |
|---|---|
| 🆘 SOS | Open **SOS** → tap **SIMULATE ACCIDENTAL FALL** → countdown → cancel with "I'M OK" or get Share/Call/Map fallback buttons |
| 🤖 Chat | Open **Chat** → type a message → RAFIQ replies (AI key configured) or the built-in accessibility fallback answers |
| 🗺️ Map | Open **Map & Places** → see demo markers → tap **Open in Google Maps** on any place to navigate |
| ✋ Sign language | Open **Sign Language** → allow camera → show a sign (e.g. open palm = Hello) → TTS speaks it |
| 🎙️ Voice | Open **Voice Assistant** → talk → RAFIQ answers aloud |
| 💊 Meds | Open **Medications** → add medicine + time → get reminders |
| ✅ Verify | Bottom bar → **Companion Score** shows points/level; points grow as you add places or chat |

## UI Design

- **Color palette:** Deep Navy (#0B1626 background), Vivid Blue (#0155F3), Cyan (#03DCE1), Teal accents — aligned with the RAFIQ logo
- **Design language:** Clean, modern, medical/accessibility-focused — inspired by Samsung Health and ChatGPT
- **Typography:** Dynamic font scaling (small/normal/large/xlarge) with multiple font families
- **Dark mode:** Deep navy backgrounds with Cyan/Teal accents

## Prerequisites

- **Android:** Android Studio (or JDK 17), SDK 35
- **Backend:** Node.js 18+, PostgreSQL 14+

## Backend Setup

```bash
cd backend
npm install

# Create the database (adjust for your Postgres install)
createdb rafiq_db
psql -c "CREATE USER rafiq_user WITH PASSWORD 'rafiq_password';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE rafiq_db TO rafiq_user;"

# Configure environment
cp .env.example .env    # edit DATABASE_URL / JWT_SECRET / PORT

# Migrate + seed demo data
npm run migrate
npm run seed

# Start the server (default: http://localhost:3000)
npm run dev
```

The backend runs the REST API on `/api` and a WebSocket server on `/ws`. See `backend/README.md` for the full endpoint list.

## Android App Setup

1. Open the project root in Android Studio.
2. Create `local.properties` with `sdk.dir` if needed.
3. Point the app at your backend in `app/src/main/java/com/example/rafiq/data/remote/api/ApiConstants.kt`:
   - `BASE_URL` and `WS_URL` default to `http://192.168.137.1:3000/...` (emulator host). Use `http://10.0.2.2:3000` for the Android emulator, or your LAN IP on a physical device.
4. **AI keys (optional but recommended):** create a local `gradle-secrets.properties` at the repo root with an **OpenAI-compatible** key for real AI replies:
   - `OPENAI_API_KEY=sk-or-...` (OpenRouter) or `sk-...` (OpenAI / other providers) — for backward compatibility `GEMINI_API_KEY=sk-...` is also accepted.
   - `OPENAI_BASE_URL=https://openrouter.ai/api/v1/chat/completions` (defaults to OpenAI); point this at any OpenAI-compatible provider (OpenRouter, Groq, Azure, local server, etc.).
   - `OPENAI_MODEL=openrouter/free` (default is `gpt-3.5-turbo`; use `liquid/lfm-2.5-2.6b:free` on OpenRouter for a free, reliable demo model).
   This file is git-ignored; without a valid key the app uses the intelligent built-in accessibility fallback. API timeouts are short (8s) so chat stays responsive even when the demo backend isn't running.
5. Build & run: `./gradlew :app:assembleDebug` or press **Run** in Android Studio.

### Demo Account

```
Email:    demo@rafiq.app
Password: demo1234
```

## Scripts

| Command               | Purpose                          |
| --------------------- | -------------------------------- |
| `npm run dev`         | Start backend (nodemon)          |
| `npm start`           | Start backend (production)       |
| `npm run migrate`     | Apply DB migrations              |
| `npm run seed`        | Insert demo user + sample data   |
| `./gradlew :app:assembleDebug` | Build debug APK       |
| `./gradlew :app:testDebugUnitTest` | Run unit tests         |
| `./gradlew :app:connectedDebugAndroidTest` | Run instrumentation tests (device required) |

## Tech Stack

- **App:** Kotlin, Jetpack Compose (Material 3), Hilt, Room, Retrofit/OkHttp, DataStore, Firebase (Realtime DB + Messaging), Google Play Services Location, OpenAI-compatible AI (configurable endpoint), MediaPipe Tasks Vision, CameraX, osmdroid (OpenStreetMap)
- **Backend:** Node.js, Express, PostgreSQL, JWT (bcryptjs + jsonwebtoken), ws, Helmet, CORS, rate limiting

## Testing & Build Status

All checks pass on every build:

- ✅ **Unit tests** (`app/src/test`) — `AccessibilityFallbackReplyTest.kt`: **10/10 pass**. Covers the built-in AI accessibility reply engine (SOS, hospitals, medications, sign language, Be My Eyes, identity, greetings, unknown input).
- ✅ **Lint** (`:app:lintDebug`) — passes. The camera permission is paired with a required-`false` `<uses-feature>` so the app installs & runs on devices without a camera.
- ✅ **Build** (`:app:assembleDebug`) — produces `app-debug.apk`.
- **Instrumented tests** (`app/src/androidTest`, device required) — `RafiqDatabaseDaoTest.kt` exercises Contact, Medication, Place, and ChatMessage Room DAOs against an in-memory database. Run with `./gradlew :app:connectedDebugAndroidTest`.
- ✅ **Backend** — migrates, seeds, and serves all REST + WebSocket endpoints against PostgreSQL (verified live: health, auth, scoreboard, places, contacts).

### Quick verification

```bash
cd backend
npm install
npm run migrate
npm run seed
npm run dev          # start server on :3000

# In another terminal (repo root)
./gradlew :app:testDebugUnitTest     # unit tests (10/10)
./gradlew :app:lintDebug             # lint
./gradlew :app:assembleDebug         # APK
```

### Demo credentials

```
Email:    demo@rafiq.app
Password: demo1234
```

> **Runtime note:** SOS, sign-language recognition, the accessible map, and the intelligent AI reply engine all work **offline / without Firebase**. If Firebase (push + realtime guardian alerts) isn't configured with your own `google-services.json`, the app degrades gracefully — SMS + Share/Call + Google-Maps SOS fallbacks still fire and the UI never crashes. The map sends a **real identifying User-Agent** (RAFIQ-Android/1.0) so `tile.openstreetmap.org` does not 403 us under the OSM tile usage policy.

## Sign Language Recognition

On-device inference via **MediaPipe Gesture Recognizer** with **CameraX** for live camera processing.

### How It Works

1. CameraX captures frames from the front-facing camera
2. Frames are converted from YUV to Bitmap and passed to MediaPipe's Gesture Recognizer
3. MediaPipe detects hand landmarks and classifies the gesture
4. Recognized gestures are mapped to display labels (e.g., "Open_Palm" → "Hello")
5. The recognized text accumulates and is spoken aloud via TTS

### Supported Gestures (10 signs)

| Gesture | Label | Description |
|---------|-------|-------------|
| Open_Palm | Hello | Open hand facing camera |
| Closed_Fist | Fist | Closed fist |
| Pointing_Up | A | Index finger pointing up |
| Thumb_Up | Yes | Thumbs up |
| Thumb_Down | No | Thumbs down |
| Victory | Peace | Two fingers up (V sign) |
| ILoveYou | I Love You | Pinky + index + thumb extended |
| *custom* | OK | Thumb + index pinched, other fingers up |
| *custom* | Rock | Index + pinky up (metal horns), others folded |
| *custom* | L | Index + thumb extended (ASL L), others folded |

The 7 model gestures come from the bundled MediaPipe model. **OK, Rock and L** are recognized by a lightweight **hand-landmark classifier** (`LandmarkGestureClassifier.kt`, 21 MediaPipe landmarks, pure-Kotlin + unit-tested) that runs on every frame and takes priority over the frozen model categories — adding 3 extra recognizable signs on top of the model's limit.

### Model Details

- **Framework:** MediaPipe Tasks Vision (`com.google.mediapipe:tasks-vision:0.10.21`)
- **Model:** `gesture_recognizer.task` bundled in `app/src/main/assets/` (download from `https://storage.googleapis.com/mediapipe-models/gesture_recognizer/gesture_recognizer/float16/1/gesture_recognizer.task`). This is a bundled model containing `hand_landmarker.task` + `hand_gesture_recognizer.task`, loaded at runtime via `BaseOptions.setModelAssetPath`.
- **Fallback:** If the model file is missing, the app shows camera preview with a warning banner instead of crashing
- **Inference mode:** Live stream (async, non-blocking)
- **Max hands:** 1
- **Confidence threshold:** 0.7

### File Structure

```
app/src/main/java/com/example/rafiq/presentation/signlanguage/
├── GestureRecognizerHelper.kt         — MediaPipe setup, YUV→Bitmap conversion, inference wrapper
├── LandmarkGestureClassifier.kt       — extra OK / Rock / L signs from hand landmarks (unit-tested)
├── SignLanguageViewModel.kt           — MVVM ViewModel, model availability check, state management
└── SignLanguageScreen.kt              — CameraX preview, recognition overlay, permission handling
```
