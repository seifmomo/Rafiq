# RAFIQ — Human Support Assistant Booking Platform for People with Disabilities

**RAFIQ** ("Rafeeq" — Arabic for *companion / friend*) is an **AI-powered Accessibility Marketplace**: a two-sided platform that connects people with disabilities to trained support assistants, while wrapping the journey in AI — sign-language recognition, voice commands, accessible navigation, emergency SOS, and personalized assistance.

> 🏆 **Competition / demo material is in [`docs/COMPETITION_PITCH.md`](docs/COMPETITION_PITCH.md)** — presentation prompt, slide-by-slide script, demo checklist, and judge Q&A.

### 📦 Presentation & Pitch Files

| File | What it is |
|---|---|
| 📊 [`docs/RAFIQ_pitch_deck.pptx`](docs/RAFIQ_pitch_deck.pptx) | **16-slide pitch deck** (AI-powered Accessibility Marketplace) — ready to present/edit |
| 📋 [`docs/CLAUDE_IMPACTX_PROMPT.md`](docs/CLAUDE_IMPACTX_PROMPT.md) | **Claude/power-user prompt** — full Strategy Coach brief with project details pre-filled + all 7 tasks executed (validation, workload, 7:45 pitch timing, Q&A, 110-pt scorecard, timeline) |
| 📈 [`docs/MAX_SCORE_PLAN.md`](docs/MAX_SCORE_PLAN.md) | **73 → 110 action plan** — per-criterion checklist to close every gap incl. both +5 bonuses (Google Integration, Presentation Efficiency), with Google Gemini wiring done in code |
| 📚 [`docs/PROJECT_DETAILS.md`](docs/PROJECT_DETAILS.md) | **All project details & data** — problem, market, solution, business model (10 streams), unit economics, impact KPIs, SDGs, tech stack, roadmap |
| 🎬 [`docs/AI_VIDEO_PROMPT.md`](docs/AI_VIDEO_PROMPT.md) | **AI video generator prompt** — 3-min pitch video + 60-sec reel + voiceover script (HeyGen/Synthesia/Runway/D-ID ready) |

---

## Mission

> Empower people with disabilities through accessible technology, human assistance, AI-powered recommendations, and personalized support.

RAFIQ is designed to be a **scalable startup** — pilot in Egypt, expand to the Middle East, then global — not a university project.

---

## What Is RAFIQ?

- **Two-sided marketplace** — Users book trained, verified, rated support assistants (wheelchair, mobility, sign-language, vision, elderly & multiple assistance).
- **AI matching engine** — recommends the best assistant, route, estimated cost, travel time, and accessibility considerations from a few inputs.
- **AI accessibility suite** — sign-language recognition, voice assistant, accessible map, emergency SOS, medication, and an AI chat that works even offline.
- **One product, two audiences** — independence for users, income + skills platform for assistants.

---

## Features

| | Feature | What it does |
|---|---|---|
| 🤝 | **Assistant Booking** | Multi-step booking flow: select needs (wheelchair, sign language, hearing, vision, elderly, multiple) → journey (from → to, date/time, hourly budget) → AI-matched assistant list → review & confirm with estimated hours + cost |
| 🧠 | **AI Matching System** | Scores assistants by skill match to your needs, rating, distance, and budget; relaxes the budget limit gracefully; estimates hours from distance (15 km/hr, min 1 hr) |
| 👤 | **Accessibility Profiles** | Disability type & needs drive recommendations; preferences are reflected across booking and AI chat |
| 🎙️ | **Voice Commands** | Speech-to-text with avatar; replies read aloud via TTS and saved to chat history |
| ✋ | **Sign Language** | CameraX + MediaPipe: **10 signs** (Fist, Hello, A, Yes, No, Peace, I Love You, OK, Rock, L) with live TTS, fully on-device |
| 🗺️ | **Navigation Assistance** | Real map (Carto Voyager basemap over OSM data) with wheelchair/sign/braille markers, auto-seeded demo places, and **Open in Google Maps** deep links |
| 🆘 | **Emergency Assistance** | 10s countdown → alert contact; always-available fallbacks: **Share SOS alert**, **Open location in Google Maps**, **Call emergency number** |
| 👨‍👩‍👧 | **Caregiver Communication** | Emergency contacts, guardian mode, live location sharing |
| 🤖 | **Personalized Accessibility Experience** | AI chat with memory, offline-aware fallback, AR/FR localization, dark mode, font scaling, TTS speed |
| 💊 | **Medication reminders** | Track and get reminded to take medication |
| 🏥 | **Hospital finder** | Find nearby accessible care |
| 🏆 | **Companion Score** | Gamified points, levels, leaderboard |
| ✅ | **Automated tests** | AI reply engine + gesture-classifier + booking-estimate unit tests |

### How to use (quick legend)

| Feature | Steps |
|---|---|
| 🤝 **Book an assistant** | Home → **Book a Human Assistant** (primary action) → pick needs + from/to + date/time + budget → **Find Assistants** → Select → **Confirm Booking** |
| 🆘 SOS | **SOS** → **SIMULATE ACCIDENTAL FALL** → countdown → cancel, or get Share/Call/Map fallback buttons |
| 🤖 Chat | **Chat** → type a message → AI replies (key configured) or the built-in accessibility fallback answers |
| 🗺️ Map | **Map & Places** → see demo markers → **Open in Google Maps** to navigate |
| ✋ Sign language | **Sign Language** → allow camera → show a sign (open palm = Hello) → TTS speaks it |
| 🎙️ Voice | **Voice Assistant** → talk → RAFIQ answers aloud |

---

## Business Model

### Revenue streams (10)

| # | Stream | How money flows |
|---|---|---|
| 1 | **Assistant Booking Commission** | 15–20% take rate per completed booking (price paid by user minus assistant payout) |
| 2 | **Premium User Subscription** | Unlimited bookings, priority matching, offline maps, advanced profiles (EGP 99/mo tiered) |
| 3 | **Premium Assistant Subscription** | Assistants pay a small monthly fee for more visibility, verified badge, scheduling tools |
| 4 | **NGO Partnerships** | Flat SaaS/white-label fee + per-booking discounts for partner organizations |
| 5 | **Hospital Partnerships** | B2B contract: discharge-to-home assistant bookings billed per covered patient |
| 6 | **Rehabilitation Center Partnerships** | Centers book recurring assistants through a dash-board; RAFIQ takes a platform fee |
| 7 | **Government Accessibility Programs** | Public-sector accessibility programs subsidize bookings (pilot-ready voucher model) |
| 8 | **Corporate Accessibility Services** | Accessibility audits + emergency assistance for corporate campuses (per-employee/year) |
| 9 | **AI Accessibility Services** | API/saas: route accessibility scores, sign-language SDK, AI accessibility reports |
| 10 | **Future Marketplace** | Equipment rental (wheelchairs, walkers, smart glasses) + assistive-device marketplace |

### Pricing / How money flows

- Users pay per booking or via subscription; assistants set per-hour rates within platform bands.
- Clear price transparency upfront: **estimated hours + estimated total before confirming** (currently estimated from distance; later from live traffic + route accessibility).
- Commission is collected at booking settlement; refunds/cancellations policy builds trust.

### Scalability

- Asset-light (no vehicles, no owned assets) — supply comes from trained assistants onboarded via verified profiles.
- Data flywheel: more bookings → better matching → better user outcomes → more users & assistants.
- Geographic expansion is config-driven: Cairo demo data generalizes to any city via repository + matching rules.

### Risks

- **Supply quality & trust** → verification, ratings, background checks, insurance partnerships.
- **Two-sided chicken-and-egg** → seed supply in one city (new Cairo) + direct NGO/hospital referrals for demand.
- **Regulatory** (Egypt disability law, labor classification of assistants) → engage MoSS / disability councils early.
- **Fraud / no-shows** → prepayment hold with auto-release after service completion.

### Advantages

- Purpose-built for PWD (not a repurposed ride-hailing/caregiving app).
- Offline-first reliability (SOS, sign language, AI replies, maps work with zero signal).
- AI matching reduces search cost vs. generic marketplaces.

---

## AI Matching System

```
Inputs
  Disability type  →  Severity  →  Location  →  Destination  →  Budget  →  Required skills
                        │
                        ▼
Matching Engine
  skill match to needs  +  distance/ETA  +  availability  +  rating  +  budget fit
                        │
                        ▼
Outputs
  Best assistant(s)      (sorted by match score, rating, distance)
  Best route / travel time
  Estimated cost         (hourly rate × estimated hours)
  Accessibility considerations (wheelchair ramps, sign availability, etc.)
```

In the app today: users pick needs, journey, date/time and budget → the engine scores and sorts the assistant pool, relaxes the budget filter if needed, and shows estimated hours + total before confirmation (`AssistantRepositoryImpl` + `AssistantBookingViewModel`). Route/travel-time inputs are the next evolution step (see Roadmap).

---

## Competitive Advantage

**RAFIQ is an "AI-powered Accessibility Marketplace" — not just a booking app.**

| vs. | What they do | RAFIQ edge |
|---|---|---|
| **Uber** | Moves people, no assistive care | Trained assistive skills, needs-matched assistants, accessibility considerations |
| **Care.com / caregiving** | Elderly care listing board | Purpose-built for PWD journeys; AI matching; on-demand; offline-first |
| **Traditional caregiving** | Private, informal, unvetted | Verified profiles, ratings, transparent pricing, digital booking |
| **Accessibility NGOs** | Advocacy & education | Commercial marketplace that delivers trained human help on demand |

---

## Investor Pitch Summary

- **Problem:** 1 in 6 people worldwide live with a disability; getting around needs specially trained human help, yet access to it is fragmented, informal, and expensive.
- **Market:** ~1.3B people with disabilities globally; Egypt alone has an estimated 20M+; aging populations and accessibility mandates grow the market yearly.
- **Solution:** An AI-powered accessibility marketplace — book trained assistants on demand with AI matching, wrapped in an offline-first accessibility suite.
- **Business model:** 10 revenue streams led by booking commission + premium subscriptions + B2B partnerships.
- **Advantage:** Purpose-built, trust-first, offline-first, AI-matched — vs Uber, Care.com, caregiving services, and NGOs.
- **Future growth:** Cairo pilot → MENA → global; assistant supply, marketplace, AI services.
- **Impact:** Independence, safety, and jobs for thousands of assistants.
- **Revenue:** Commission + subscriptions (users & assistants) + NGO/Hospital/Government/Corporate contracts + AI services.

---

## Future Roadmap (ranked)

| Priority | Feature | Impact | Difficulty | Revenue potential |
|---|---|---|---|---|
| 1 | **Real-time assistant tracking (live map)** | High | Medium | High (premium tier) |
| 2 | **Accessibility Score for locations** | High | Medium | High (AI services + B2B audits) |
| 3 | **Live traffic-aware ETA & cost** | High | Low–Med | Medium (booking conversion) |
| 4 | **AI Accessibility Assistant (advanced)** | High | Medium | Medium (subscription) |
| 5 | **Accessibility community** | Medium | Medium | Low (retention) |
| 6 | **Accessibility Marketplace (rentals/devices)** | Medium | High | High (marketplace fees) |
| 7 | **Smart accessibility recommendations** | Medium | Medium | Medium (personalization) |
| 8 | **Emergency support in-app (guardian dashboard)** | High | Medium | Medium (B2B) |

### Where these appear in the product

- **Home Screen:** Assistant Booking is the primary hero action; quick actions + AI assistant + tools below the hero in a compact, professional layout.
- **Assistant Booking:** Registration, needs selection, journey, budget, matched results, confirm (built).
- **Profile (future):** Accessibility profile, subscription tier, booking history.
- **Settings (future):** Premium user plan, language, notifications, payment methods.
- **Admin Dashboard (future):** Assistant onboarding/verification, commission ledger, location accessibility audits, partner contracts.

---

## Repo Layout

```
rafiq/
├── app/          Android app (Kotlin, Jetpack Compose, Hilt, Room)
│   └── src/main/java/com/example/rafiq/
│       ├── domain/model/            domain models (Assistant, DisabilityNeed, BookingRequest, Booking)
│       ├── domain/repository/       repository interfaces (AssistantRepository, PlaceRepository)
│       ├── data/repository/         implementations + matching engine + sample Cairo assistant pool
│       ├── presentation/assistant/  AssistantBookingViewModel + AssistantBookingScreen (form → results → confirm)
│       ├── presentation/home/       Home screen with compact hero + primary booking action
│       ├── presentation/signlanguage/  CameraX 🔗 MediaPipe gestures + landmark classifier (OK/Rock/L)
│       └── ui/components/           reusable packaging (BookingComponents, RafiqComponents)
├── backend/      REST + WebSocket API (Express, PostgreSQL, JWT)
└── gradle/       Gradle wrapper config
```

---

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
   - `OPENAI_API_KEY=sk-or-...` (OpenRouter) or `sk-...` (OpenAI / other providers).
   - **Google Gemini:** set `GEMINI_API_KEY=AIza...` (Gemini keys start with `AIza`). When present, RAFIQ uses Google's Gemini endpoint (`GEMINI_BASE_URL`, default `https://generativelanguage.googleapis.com/v1beta/openai/chat/completions`) with model `GEMINI_MODEL` (default `gemini-2.5-flash`) — path to the +5 Google Integration bonus. Gemini keys work without any OpenAI key.
   - `OPENAI_BASE_URL=https://openrouter.ai/api/v1/chat/completions` (defaults to OpenAI); point this at any OpenAI-compatible provider (OpenRouter, Groq, Azure, local server, etc.).
   - `OPENAI_MODEL=liquid/lfm-2.5-2.6b:free` (default `gpt-3.5-turbo`).
   This file is git-ignored; without a valid key the app uses the intelligent built-in accessibility fallback. API timeouts are short (8s) so chat stays responsive even when the demo backend isn't running.
5. Build & run: `./gradlew :app:assembleDebug` or press **Run** in Android Studio. **Reinstall the app** to pick up the latest map/tile changes.

### Demo Account

```
Email:    demo@rafiq.app
Password: demo1234
```

## Screens & Scripts

| Command | Purpose |
|---|---|
| `npm run dev` | Start backend (nodemon) |
| `npm start` | Start backend (production) |
| `npm run migrate` | Apply DB migrations |
| `npm run seed` | Insert demo user + sample data |
| `./gradlew :app:assembleDebug` | Build debug APK |
| `./gradlew :app:testDebugUnitTest` | Run unit tests |
| `./gradlew :app:connectedDebugAndroidTest` | Run instrumentation tests (device required) |

**Pitch / video generation:** the `.pptx` in `docs/` was generated with `python-pptx` from the same build script used for the original deck; the `docs/AI_VIDEO_PROMPT.md` prompt produces the pitch video from the deck content.

## UI Design Language

- **Color palette:** Deep Navy (`#0B1626` background), Vivid Blue (`#0155F3`), Cyan (`#03DCE1`), Teal accents — aligned with the RAFIQ logo
- **Design language:** Clean, modern, accessibility-first — inspired by Samsung Health and ChatGPT
- **Typography:** Dynamic font scaling (small/normal/large/xlarge) with multiple font families
- **Dark mode:** Deep navy backgrounds with Cyan/Teal accents

## Tech Stack

- **App:** Kotlin, Jetpack Compose (Material 3), Hilt, Room, Retrofit/OkHttp, DataStore, Firebase (Realtime DB + Messaging), Google Play Services Location, **Google Gemini API** (default `gemini-2.5-flash`, OpenAI-compatible) or any OpenAI-compatible endpoint, **MediaPipe Tasks Vision**, CameraX, osmdroid (Carto Voyager tiles over OpenStreetMap data)
- **Backend:** Node.js, Express, PostgreSQL, JWT (bcryptjs + jsonwebtoken), ws, Helmet, CORS, rate limiting

> **Google Integration story (IMPACT X +5 bonus):** MediaPipe (on-device sign-language, already shipped), Gemini API (AI assistant replies), Firebase (messaging/analytics), and Google Maps deep links make Google the engine of the product — not a logo.

## Testing & Build Status

All checks pass on every build:

- ✅ **Unit tests** (`app/src/test`): **18/18 pass** — `AccessibilityFallbackReplyTest.kt` (10/10: SOS, hospitals, medications, sign language, vision, identity, greetings, unknown input) + `LandmarkGestureClassifierTest.kt` (8/8: OK/Rock/L hand-landmark classification).
- ✅ **Lint** (`:app:lintDebug`) — passes. The camera permission is paired with a required-`false` `<uses-feature>` so the app installs & runs on devices without a camera.
- ✅ **Build** (`:app:assembleDebug`) — produces `app-debug.apk`.
- **Instrumented tests** (`app/src/androidTest`, device required) — `RafiqDatabaseDaoTest.kt` exercises Contact, Medication, Place, and ChatMessage Room DAOs against an in-memory database. Run with `./gradlew :app:connectedDebugAndroidTest`.
- ✅ **Backend** — migrates, seeds, and serves all REST + WebSocket endpoints against PostgreSQL (verified live: health, auth, scoreboard, places, contacts).

> **Runtime note:** SOS, sign-language recognition, the accessible map, and the intelligent AI reply engine all work **offline / without Firebase**. If Firebase isn't configured with your own `google-services.json`, the app degrades gracefully — SMS + Share/Call + Google-Maps SOS fallbacks still fire and the UI never crashes. The map loads **Carto Voyager (OSM data)** tiles with a real identifying User-Agent instead of `tile.openstreetmap.org`, which 403s demos under its tile usage policy.

## Assistant Booking — Implementation Notes

- **Domain:** `DisabilityNeed` (6 needs), `Assistant`, `BookingRequest`, `Booking` in `domain/model`.
- **Matching:** `AssistantRepositoryImpl` scores each assistant by how many selected needs their skills cover, then sorts by score → rating → distance; budget filter relaxes gracefully if nothing qualifies.
- **Estimates:** hours = distance/15 km (min 1), rounded to half-hours; cost = hours × hourly rate — shown before confirmation and on the confirmation screen.
- **Sample data:** 8 demo Cairo assistants (wheelchair, sign-language, vision, elderly, hearing, all-round) — replace with the live backend pool.

## Sign Language Recognition

On-device inference via **MediaPipe Gesture Recognizer** with **CameraX** for live camera processing.

### How It Works

1. CameraX captures frames from the front-facing camera
2. Frames are converted from YUV to Bitmap and passed to MediaPipe's Gesture Recognizer
3. MediaPipe detects hand landmarks and classifies the gesture
4. Recognized gestures are mapped to display labels (e.g., "Open_Palm" → "Hello")
5. The recognized text accumulates and is spoken aloud via TTS

### Supported Gestures (10 signs)

| Emoji | Gesture | Label | Description |
|-------|---------|-------|-------------|
| 🖐️ | Open_Palm | Hello | Open hand facing camera |
| ✊ | Closed_Fist | Fist | Closed fist |
| ☝️ | Pointing_Up | A | Index finger pointing up |
| 👍 | Thumb_Up | Yes | Thumbs up |
| 👎 | Thumb_Down | No | Thumbs down |
| ✌️ | Victory | Peace | Two fingers up (V sign) |
| 🤟 | ILoveYou | I Love You | Pinky + index + thumb extended |
| 👌 | *custom* | OK | Thumb + index pinched, other fingers up |
| 🤘 | *custom* | Rock | Index + pinky up (metal horns), others folded |
| 👆 | *custom* | L | Index + thumb extended (ASL L), others folded |

The 7 model gestures come from the bundled MediaPipe model. **OK, Rock and L** are recognized by a lightweight **hand-landmark classifier** (`LandmarkGestureClassifier.kt`, 21 MediaPipe landmarks, pure-Kotlin + unit-tested) that runs on every frame and takes priority over the frozen model categories.

### Model Details

- **Framework:** MediaPipe Tasks Vision (`com.google.mediapipe:tasks-vision:0.10.21`)
- **Model:** `gesture_recognizer.task` bundled in `app/src/main/assets/` (download from `https://storage.googleapis.com/mediapipe-models/gesture_recognizer/gesture_recognizer/float16/1/gesture_recognizer.task`).
- **Fallback:** If the model file is missing, the app shows camera preview with a warning banner instead of crashing
- **Inference mode:** Live stream (async, non-blocking) · **Max hands:** 1 · **Confidence threshold:** 0.7