# RAFIQ — Full Project Details & Data File

> Master reference for the whole project. Use this for the pitch deck, the AI video, the demo, and team coordination.

---

## 1. IDENTITY

| Field | Value |
|---|---|
| Product name | **RAFIQ** (Arabic: رفيق = "companion / friend") |
| Category | AI-powered Accessibility Marketplace (two-sided platform) |
| One-liner | "Trained human assistance, matched by AI — independence for users, income for assistants." |
| Platform | Android app (offline-first) |
| Repo | https://github.com/seifmomo/Rafiq |
| Color identity | Dark navy `#0B1626`, cyan `#03DCE1`, blue `#0155F3` |
| Languages | UI in EN / AR / FR |

---

## 2. PROBLEM (why we exist)

- **1 in 6 people** worldwide live with a disability (WHO, ~16% of humanity = **~1.3B people**).
- Egypt: est. **10–15M+ people with disabilities**; MENA est. **60M+**.
- People with disabilities face daily barriers: navigating cities, communicating, remembering medication, getting help fast.
- **The hardest gap: trained human help on demand.** Assistance today is informal, unvetted, expensive, and has no platform or transparency.
- Existing apps are **fragmented** — a map app, a reminder app, an SOS app. Nothing ties assistance together with AI.
- Families burn out from 24/7 caregiving with no relief, no transparency, no accountability.

---

## 3. SOLUTION (what we built)

One product, two moving parts:

### A. The Marketplace
- Book trained support assistants: wheelchair, mobility, sign language, hearing, vision, elderly & multiple needs.
- Verified profiles, community ratings, transparent hourly rates.
- Needs-matched search: disability type → location → destination → budget.
- Cost estimate with estimated hours shown **before** you commit.

### B. The AI Suite
- **Real-time Sign Language** — 10 gestures, on-device via Google MediaPipe, **no internet, 100% private**.
- **Voice & Chat AI** — talk or type; intelligent offline smart fallback so it never goes silent.
- **Accessible Map** — wheelchair/sign/braille-place markers (OpenStreetMap/Carto data) + Google Maps links.
- **Emergency SOS** — 10s cancelable countdown → SMS/share/call/maps fallbacks + live location.
- **Medication & Health** — reminders, hospital finder.
- **Companion Score** — gamified points/levels/leaderboard for retention.

---

## 4. ASSISTANT BOOKING ENGINE (core differentiator)

**Flow: Define needs → Plan journey → AI matches → Review & confirm.**

- **Inputs:** disability need (enum: wheelchair, vision, hearing, sign-language, elderly, mobility escort, multiple), pickup location, destination, preferred date/time, hourly budget.
- **Matching score:** skill match → rating → distance, then budget relaxation; estimate = distance / 15 km per hour (min 1 hr), rounded to half-hours × assistant hourly rate.
- **State machine:** FORM → RESULTS → SUMMARY → CONFIRMED.
- Sample data: 8 seeded assistants in Cairo with varied skills/ratings/routes/rates.

---

## 5. FEATURES STATUS (live in app)

| Feature | Status |
|---|---|
| Assistant Booking (needs → journey → confirm) | ✅ Live, end-to-end |
| AI Matching engine | ✅ Live |
| Sign Language (10 gestures, on-device) | ✅ Live |
| Voice & Chat AI + offline fallback | ✅ Live |
| Accessible Map (Cairo places) | ✅ Live |
| Emergency SOS | ✅ Live |
| Medication & Health | ✅ Live |
| Companion Score | ✅ Live |
| Dark mode / font scaling / AR / FR | ✅ Live |

**Quality gates:** 18/18 unit tests passing (AI reply fallback 10 + landmark gesture classifier 8) · lint clean · debug APK builds.

---

## 6. TECHNOLOGY STACK

| Layer | Tech |
|---|---|
| UI | Kotlin, Jetpack Compose, Material 3 |
| Architecture | Clean architecture: domain/model → domain/repository → data/repository → presentation (MVVM) |
| DI / DB | Hilt, Room, DataStore, Coroutines |
| On-device AI | **Google MediaPipe Tasks Vision** + CameraX (10 sign gestures + custom landmark classifier) |
| Generative AI | OpenAI-compatible endpoint (OpenRouter/Groq/Azure/local) + deterministic offline fallback |
| Maps | osmdroid + Carto Voyager (OSM data) — **swap target: Google Maps SDK for the +5 bonus** |
| Backend | Node.js, Express, PostgreSQL, WebSocket, JWT, bcrypt, Helmet, CORS |
| Google integration (bonus plan) | **Google Maps Platform (Directions + Places)** for ETA + **Gemini API** for AI replies + **Firebase** Analytics/Crashlytics |
| Privacy | On-device ML (no data leaves device), JWT auth, secrets never committed |

---

## 7. MARKET OPPORTUNITY

| Dimension | Value |
|---|---|
| Global | ~1.3B people with disabilities (WHO) |
| Egypt | Est. 10–15M+ people with disabilities |
| MENA | 60M+ potential users; aging population grows demand |
| TAM | Global accessible assistance (pitch estimate $60B+) |
| Tailwinds | Accessibility mandates, disability tourism, corporate ESG budgets |

---

## 8. BUSINESS MODEL — 10 REVENUE STREAMS

1. **Booking Commission** — 15–20% per completed booking.
2. **Premium User Subscription** — unlimited bookings, priority matching, offline maps.
3. **Premium Assistant Subscription** — visibility, verified badge, scheduling tools.
4. **NGO Partnerships** — SaaS / white-label + per-booking rates.
5. **Hospital Partnerships** — discharge-to-home bookings, B2B contract.
6. **Rehab Center Partnerships** — recurring bookings via partner dashboard.
7. **Gov Accessibility Programs** — voucher-subsidized bookings, public projects.
8. **Corporate Accessibility** — campus audits + emergency assistance (B2B).
9. **AI Accessibility Services** — accessibility scores, sign-language SDK, API.
10. **Future Marketplace** — equipment rental & assistive-device sales.

### Money flow
User pays per booking or premium tier · estimated hours × rate shown before confirm · platform takes 15–20% at settlement · assistants free to join, premium tier for visibility · B2B per-booking or subscription · **growth loop: bookings → data → better matching → more users & assistants**.

### Unit economics (pitch numbers)
- Avg booking 120 EGP → 15% commission = 18 EGP/booking.
- Target: 1,000 bookings/month in Year 1 = ~18,000 EGP/month gross revenue.
- Assistants earn flexible income; users save hours of telephone-chasing.

---

## 9. IMPACT & SDG ALIGNMENT

**SDGs targeted:** SDG 3 (Good Health) · SDG 8 (Decent Work & Economic Growth) · SDG 10 (Reduced Inequalities) · SDG 11 (Sustainable Cities).

**Impact KPIs (to show in demo):**
- Time-to-book reduced from hours/days (phone calls) to **< 2 minutes**.
- Booking match success rate.
- Assistant income per booking.
- Repeat rate / assistant retention.
- On-device sign-language accuracy (98%+).
- Reach: Cairo pilot (50 users + 15 assistants) → district scale.

---

## 10. FEASIBILITY & EXECUTION

- **Ready:** working app, working booking engine, offline ML, clean repo, 18/18 tests.
- **Gaps & fixes:**
  1. Assistant supply is seeded → add "become an assistant" onboarding + Google sign-in verification.
  2. No real payment gateway → Paymob / Google Pay (test mode) in roadmap.
  3. Map layer osmdroid → Google Maps SDK (also earns +5 bonus).
  4. Booking persistence is local Room → roadmap to Firebase/Firestore sync.
- **Roadmap ranked by impact/difficulty/revenue:** real-time assistant tracking, Accessibility Score for locations, live traffic-aware ETA & cost, advanced AI assistant, accessibility community, rental marketplace, smart accessibility recommendations, emergency guardian dashboard.

---

## 11. SUSTAINABILITY & POST-HACKATHON

- **Phase 1 (3 months):** Cairo pilot — onboard 50 assistants + 3 hospital/rehab partners; free-trial premium tier.
- **Phase 2 (6–12 mo):** live tracking + Accessibility Score; Cairo & Alexandria; GCC government pilots.
- **Phase 3 (12–24 mo):** AI services API, assistive-device marketplace, MENA scaling.
- **Funding shortlist:** Google for Startups, Injaz Accelerator, AUC V-Lab, Misr El Kheir accessibility program, Sawiris Foundation.
- **Tech continuity:** Seif owns the codebase; Malak publishes sustainability roadmap; monthly demo milestone.

---

## 12. TEAM & WORKLOAD

| Member | Role |
|---|---|
| **Seif** | Lead Developer, Architecture, Google Integration, Demo, Impact Measurement |
| **Jana** | Team Leader, Impact Strategist, SDG Alignment, Problem Definition, Presentation Flow |
| **Hana** | UX/Design Lead, User Research, Accessibility, Journey, Visual Design |
| **Nour** | Business Analyst, ROI Model, Market Research, Funding Strategy |
| **Malak** | Sustainability Lead, Post-Hackathon Roadmap, Scalability, Q&A Prep |

---

## 13. COMPETITIVE ADVANTAGE vs ALTERNATIVES

| Competitor | Gap | RAFIQ |
|---|---|---|
| Uber / ride-hailing | Moves people, no assistive care | Trained assistive skills, needs-matched assistants, accessibility-aware routing |
| Care.com / caregiving | Elderly-care listing board | Purpose-built for PWD; on-demand booking with cost transparency |
| Traditional caregiving | Private, informal, unvetted | Verified profiles, ratings, insurance-ready workflow, digital ledger |
| Accessibility NGOs | Advocacy & education | A commercial marketplace delivering trained help on demand |

---

## 14. KEY MESSAGES / QUOTES

- Mission: "Empower people with disabilities — technology + human help + AI."
- Closing: **"Accessibility isn't a feature. It's the whole product."**
- Into: "In three taps, book a trained, verified support assistant — matched by AI to your needs, your route, and your budget."
- Product name first: "RAFIQ (Rafeeq) — Arabic for companion / friend."

---

## 15. SOURCES OF FACTS

- WHO: ~1.3B people / 1-in-6 with disability.
- Egypt: CAPMAS (est. 10–15M+ people with disabilities) — pitch estimate, phrased as estimate.
- Market shape figures are **pitch estimates** — present as directional, not audited.