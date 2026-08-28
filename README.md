# RAFIQ — Your Companion, Every Step

An accessibility companion for people with disabilities: real-time guidance, emergency SOS, AI chat assistance, navigation, learning tools, and gamified rewards. Built with an Android (Kotlin + Jetpack Compose) app and a Node.js/Express/PostgreSQL backend.

## Repo Layout

```
rafiq/
├── app/          Android app (Kotlin, Jetpack Compose, Hilt, Room)
├── backend/      REST + WebSocket API (Express, PostgreSQL, JWT)
└── gradle/       Gradle wrapper config
```

## Features

- **Emergency SOS** — 10s countdown, SMS + Firebase alert to emergency contact, guardian mode, fall-detection simulation with vibration/TTS
- **AI Chat Assistant** — conversation UI with typing indicator, clear-history, cloud sync, Gemini integration
- **Map & Equipped Places** — add wheelchair/sign-language/braille-equipped places (+50 pts)
- **Voice Assistant** — speech-to-text with accessibility avatar
- **Sign Language Recognition** — real-time hand gesture recognition using CameraX + MediaPipe Tasks Vision (recognizes Fist, Open Palm, Pointing Up, Thumb Up/Down, Victory, ILY gestures)
- **Companion Score** — gamified points, levels, leaderboard
- **Contacts, Medication reminders, Hospital finder, Learning center, Awareness & rights**
- **Be My Eyes** — simulated live volunteer camera
- **Smart Glasses** — mock BLE hardware connection + obstacle distance warning
- **Accessibility** — AR/FR localization, dark/light/system themes, font size & family scaling, TTS speech rate
- **Guest mode, JWT auth, backup/restore**

## UI Design

- **Color palette:** Teal (#14B8A6), Cyan (#06B6D4), Dark Navy (#0F172A), White surfaces
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
4. (Optional) Set a Gemini API key in `gradle.properties` as `GEMINI_API_KEY=...` for real AI replies.
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

## Tech Stack

- **App:** Kotlin, Jetpack Compose (Material 3), Hilt, Room, Retrofit/OkHttp, DataStore, Firebase (Realtime DB + Messaging), Google Play Services Location, Gemini SDK, MediaPipe Tasks Vision, CameraX
- **Backend:** Node.js, Express, PostgreSQL, JWT (bcryptjs + jsonwebtoken), ws, Helmet, CORS, rate limiting

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
