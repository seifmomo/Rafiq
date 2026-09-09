# RAFIQ — Competition Presentation Kit

> Everything you need to build your slides and rehearse a winning demo.
> Use this as your prompt for an AI deck-builder (Gamma, Canva.ai, PowerPoint Designer, Tome) and as your speaking script.
> The updated business pitch deck is **`RAFIQ_deck_v2.pptx`** (16 slides, "AI-powered Accessibility Marketplace").

> 📌 **Using Claude?** See [`CLAUDE_DECK_PROMPT.md`](CLAUDE_DECK_PROMPT.md) for a ready-to-paste prompt that generates a polished HTML deck.

---

## 1. THE HOOK (first 30 seconds)

**One-liner:**
> *"RAFIQ is an AI-powered Accessibility Marketplace. In three taps you can book a trained, verified support assistant — wheelchair, sign language, vision or elderly care — matched by AI to your needs, your route and your budget. Around that, one offline-first companion: sign language in real time, accessible maps, emergency SOS, and AI help."*

**Open with a live demo** of the **Assistant Booking** flow: pick needs → journey → "Find Assistants" → confirm. This is the WOW moment that proves it's a *business*, not just an app.

---

## 2. THE PROBLEM (1 slide)

- **1 in 6 people** worldwide live with a disability (WHO); **~1.3B** people globally.
- Daily barriers: navigating cities, communicating, remembering medication, and getting help fast.
- Getting **trained human help** is the hardest gap — assistance is informal, unvetted, expensive.
- Existing apps are **fragmented** — a map app, a reminder app, an SOS app. Nothing ties assistance together with **AI**.

---

## 3. THE SOLUTION (1 slide)

**An AI-powered Accessibility Marketplace — one product, two moving parts:**

1. 🤝 **Assistant Booking** — needs → journey (from/to, date/time, budget) → AI-matched assistants → confirm with estimated hours & cost.
2. 🧠 **AI Matching System** — skill match + distance + rating + budget → best assistant, route, cost, accessibility notes.
3. 🆘 **Emergency SOS** — 10s cancelable countdown → SMS/share/call/maps fallbacks + live location.
4. 🖐️ **Real-Time Sign Language** — on-device MediaPipe, **10 signs**, no internet, 100% private.
5. 🗺️ **Accessible Map** — Carto Voyager (OSM data) of Cairo with wheelchair/sign/braille markers + Google Maps links.
6. 🤖 **AI Voice Assistant & Chat** — talk or type, reads aloud; offline smart fallback.
7. 💊 **Medication & Health** — reminders, hospital finder, learning center. Plus **Companion Score** gamification.

---

## 4. THE TECH (1 slide — keep visual, don't read code)

**Android app:** Kotlin, Jetpack Compose (Material 3), Hilt, Room, DataStore, Coroutines
**On-device AI:** MediaPipe Tasks Vision + CameraX (real-time gestures + custom OK/Rock/L landmark classifier)
**Generative AI:** any OpenAI-compatible endpoint (OpenRouter / Groq / Azure / local), with an intelligent offline accessibility fallback
**Maps:** osmdroid + Carto Voyager tiles (OSM data — avoids OSM's strict 403 tile policy)
**Backend:** Node.js, Express, PostgreSQL, WebSocket, JWT

**Key engineering highlights (use on the judging slide):**
- **Clean architecture** — `domain/model` → `domain/repository` → `data/repository` → `presentation` (MVVM + Hilt)
- **Assistant marketplace** — domain models (`Assistant`, `DisabilityNeed`, `BookingRequest`, `Booking`), matching engine, cost estimation
- Room for offline storage + cloud sync; **offline-first** everywhere
- **18/18 unit tests passing** + lint clean + APK builds; instrumented Room DAO tests
- Graceful degradation (no crash if Firebase/network/AI model is missing)

---

## 5. THE DEMO (this is the most important — rehearse!)

**Total: 3–5 minutes.** Have a backup device/recording. **Open with the booking flow**, then the wow features:

| # | What you show | What to say |
|---|---|---|
| 1 | **Book an Assistant** (home primary card) → pick needs (e.g. Wheelchair + Sign Language) → from/to → date/time → budget → Find Assistants → Select → Confirm | "RAFAIQ matches trained support assistants to your needs and budget — cost estimated before you commit. This is our marketplace — and it's how we make money." |
| 2 | Sign-language tab, show "Thumbs Up" → "Yes" spoken | "Real-time, on-device — no internet needed, 100% private." |
| 3 | SOS with 10s countdown (cancel fast so it doesn't actually fire) | "Help in 10 seconds — it texts the emergency contact and shares live location, or offers Share/Call/Maps fallbacks." |
| 4 | Map with accessible-place markers; tap **Open in Google Maps** | "Live OpenStreetMap with curated accessible places. Users can add new ones for points." |
| 5 | Ask the AI: *"where is the nearest hospital?"* | "AI assistant with memory, voice input and TTS output — works offline too." |
| 6 | Set a medication reminder | "Health & medication reminders so users never miss a dose." |
| 7 | Switch to dark mode / Arabic (quick, visual) | "Accessible by design — themes, font scaling, AR/FR localization." |

> **Never** demo a feature you can't guarantee live. Record every demo as a backup video; if a live step fails, switch to the clip and keep going.

---

## 6. THE BUSINESS (what judges now love to hear)

- **Revenue:** 15–20% booking commission + premium user/assistant subscriptions + NGO, hospital, rehab-center, government & corporate contracts + AI services API → all in the pitch deck.
- **Matching flywheel:** more bookings → better data → better matching → more users & assistants.
- **Why it works:** asset-light, trust-first (verified profiles + ratings), offline-reliable, purpose-built for PWD.
- **Stage plan:** Cairo pilot (50 assistants + 3 partners) → MENA → global.

---

## 7. THE IMPACT & "WHY WE WIN" (closing slide)

- **Real social impact** — independence, safety, a voice, and **jobs for trained assistants**.
- **A real business** — two-sided marketplace with 10 revenue streams, not a school project.
- **Truly integrated** — marketplace + AI + maps + SOS + health in ONE experience.
- **Offline-capable** — most competitor demos die without internet; ours doesn't.
- **Production-grade engineering** — clean architecture, 18/18 tests, real backend, real map, on-device ML.

**Closing line:**
> *"RAFIQ is an AI-powered Accessibility Marketplace — trained human assistance, matched by AI. Accessibility isn't a feature for us; it's the whole product."*

---

## 8. PRESENTATION PROMPT (paste into any AI deck-builder)

```
Create a modern, clean, professional startup-pitch deck (16:9) for RAFIQ,
an AI-powered Accessibility Marketplace for people with disabilities.

Style: accessibility/health-tech, dark-navy (#0B1626) with cyan (#03DCE1) and
blue (#0155F3) accents. Minimal text, big visuals, card-based.

Slides:
1. Title — "RAFIQ — Human Support Assistant Booking Platform". Tagline: An AI-powered Accessibility Marketplace.
2. The Problem — 1 in 6 (WHO), fragmented tools, no trained help on demand.
3. Market Opportunity — 1.3B global, Egypt 20M+, MENA 60M+; TAM/SAM/SOM.
4. Solution — two-sided marketplace + AI accessibility suite.
5. Product (live) — Assistant Booking, AI Matching, Sign Language, Voice/Chat, Map, SOS, Medication, Score.
6. AI Matching System — inputs (disability, severity, location, destination, budget, skills) → engine → outputs (assistant, route, cost, time, accessibility notes).
7. Business Model — 10 revenue streams (commission, subscriptions, NGO/hospital/rehab/gov/corporate, AI services, marketplace).
8. Money Flow & Pricing — transparency, take rate, B2B, growth loop.
9. Competitive Advantage — vs Uber, Care.com, caregiving, NGOs.
10. Technology — Kotlin/Compose/Hilt/Room, on-device MediaPipe, Carto map, OpenAI-compatible + offline fallback, Node backend.
11. Future Roadmap — ranked by impact/difficulty/revenue.
12. Impact — independence, safety, a voice, jobs.
13. The Ask — Cairo pilot → MENA → global.
14. Closing — "Accessibility isn't a feature. It's the whole product."
```

---

## 9. COMMON JUDGE QUESTIONS (prepare answers)

- **"Where's the business model / how do you make money?"** → 10 revenue streams; core = 15–20% booking commission; premium tiers for users & assistants; B2B contracts (hospitals, rehabs, NGOs, corporates, government); future AI services API + device marketplace.
- **"Why can't Uber or Care.com do this?"** → They move people or list caregivers; they don't match *assistive skills* to disability needs with accessibility-aware recommendations. RAFIQ is purpose-built and includes the accessibility suite around the booking.
- **"How is the assistant vetted?"** → Verified profiles, skills-based matching, ratings/reviews; phase-1 pilot uses partnered hospitals/rehab centers for onboarding; insurance and background-check path in roadmap.
- **"Scalability?"** → Stateless REST + PostgreSQL + WebSocket; asset-light supply side; config-driven city expansion.
- **"Security?"** → JWT auth, bcrypt hashing, Helmet, CORS, rate limiting; secrets never committed.
- **"Is it actually accessible?"** → Dark mode, dynamic font size/family, AR/FR localization, TTS output, voice input.
- **"How did you test?"** → 18/18 passing unit tests (AI reply engine + gesture classifier + booking estimates) + instrumented Room DAO tests; backend tested live; lint clean; APK builds.

---

## 10. DEMO DAY CHECKLIST (print this)

- [ ] Backend running: `npm run dev` in `backend/`
- [ ] PostgreSQL service running (`rafiq_db`, tables migrated)
- [ ] App built & installed on device (or emulator) — **reinstall to pick up the latest map/UI fixes**
- [ ] Login with **demo@rafiq.app / demo1234**
- [ ] Warm up the **Assistant Booking** flow (needs + journey prefilled) so it's one tap during the pitch
- [ ] Backup recordings of every demo step ready
- [ ] Internet available (for AI + map tiles) OR confirm offline features
- [ ] Volume ON (TTS + sign language voice) — test before you present
- [ ] Airtel/portable hotspot as backup connectivity
- [ ] Rehearsed out loud at least 3 times; timed to 3–5 min

---

## 11. TERMINOLOGY / PRONUNCIATION / FACTS

- Product name: **RAFIQ** ("Rafeeq" — Arabic for *companion/friend*). Lead with that meaning — it's powerful.
- **Accessibility Marketplace**: two-sided — users book trained assistants; assistants earn from a skills platform.
- **AI Matching**: engine that scores skills, distance, rating and budget → recommended assistant, route, cost, accessibility notes.
- **MediaPipe**: Google's on-device ML framework (privacy: no data leaves the device).
- **CameraX**: Android's camera API used for the live feed.
- **Companion Score**: gamified points/levels — keeps users engaged (retention story).
- Numbers you can cite: 1 in 6 people disabled (WHO), ~1.3B globally, an estimated 20M+ in Egypt, 60M+ in MENA.