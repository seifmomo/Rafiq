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
- **Voice & Sign Language** — speech-to-text assistant with accessibility avatar
- **Companion Score** — gamified points, levels, leaderboard
- **Contacts, Medication reminders, Hospital finder, Learning center, Awareness & rights**
- **Be My Eyes** — simulated live volunteer camera
- **Smart Glasses** — mock BLE hardware connection + obstacle distance warning
- **Accessibility** — AR/FR localization, dark/light/system themes, font size & family scaling, TTS speech rate
- **Guest mode, JWT auth, backup/restore**

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

- **App:** Kotlin, Jetpack Compose (Material 3), Hilt, Room, Retrofit/OkHttp, DataStore, Firebase (Realtime DB + Messaging), Google Play Services Location, Gemini SDK
- **Backend:** Node.js, Express, PostgreSQL, JWT (bcryptjs + jsonwebtoken), ws, Helmet, CORS, rate limiting
