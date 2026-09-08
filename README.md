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

- **Emergency SOS** — 10s countdown, SMS + Firebase alert to emergency contact, guardian mode; always-available fallbacks after triggering: **Share SOS alert**, **Open location in Google Maps**, **Call emergency number**
- **AI Chat Assistant** — conversation UI with memory, one-tap clear chat (works instantly, cloud deletes run in background), and AI replies via any **OpenAI-compatible** provider (`sk-...` key + configurable endpoint), with an always-on local accessibility fallback (SOS, hospitals, medications, sign language, Be My Eyes, glasses, companion)
- **Real Map & Equipped Places** — real **OpenStreetMap** (osmdroid) of Cairo with markers for wheelchair/sign-language/braille-equipped places (+50 pts); demo places are auto-seeded on first run so the map is never empty, and every place offers an **Open in Google Maps** deep link (works even with no map tiles)
- **Voice Assistant** — speech-to-text with accessibility avatar; responses read aloud via TTS and saved to chat history
- **Sign Language Recognition** — real-time hand gesture recognition using CameraX + MediaPipe Tasks Vision (recognizes Fist, Open Palm, Pointing Up, Thumb Up/Down, Victory, ILY gestures) with live TTS feedback
- **Companion Score** — gamified points, levels, leaderboard
- **Contacts, Medication reminders, Hospital finder, Learning center (real ASL + mobility videos), Awareness & rights**
- **Be My Eyes** — simulated live volunteer camera (clearly labeled as a demo)
- **Accessibility** — AR/FR localization, dark/light/system themes, font size & family scaling, TTS speech rate
- **Guest mode, JWT auth, backup/restore**
- **Automated tests** — unit tests for the AI accessibility reply engine + instrumented Room DAO tests

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

> **Runtime note:** SOS, sign-language recognition, the accessible map, and the intelligent AI reply engine all work **offline / without Firebase**. If Firebase (push + realtime guardian alerts) isn't configured with your own `google-services.json`, the app degrades gracefully — SMS + Share/Call + Google-Maps SOS fallbacks still fire and the UI never crashes.

## Sign Language Recognition

On-device inference via **MediaPipe Gesture Recognizer** with **CameraX** for live camera processing.

### How It Works

1. CameraX captures frames from the front-facing camera
2. Frames are converted from YUV to Bitmap and passed to MediaPipe's Gesture Recognizer
3. MediaPipe detects hand landmarks and classifies the gesture
4. Recognized gestures are mapped to display labels (e.g., "Open_Palm" → "Hello")
5. The recognized text accumulates and is spoken aloud via TTS

### Supported Gestures

| Gesture | Label | Description |
|---------|-------|-------------|
| Open_Palm | Hello | Open hand facing camera |
| Closed_Fist | Fist | Closed fist |
| Pointing_Up | A | Index finger pointing up |
| Thumb_Up | Yes | Thumbs up |
| Thumb_Down | No | Thumbs down |
| Victory | Peace | Two fingers up (V sign) |
| ILoveYou | I Love You | Pinky + index + thumb extended |

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
├── GestureRecognizerHelper.kt   — MediaPipe setup, YUV→Bitmap conversion, inference wrapper
├── SignLanguageViewModel.kt     — MVVM ViewModel, model availability check, state management
└── SignLanguageScreen.kt        — CameraX preview, recognition overlay, permission handling
```
