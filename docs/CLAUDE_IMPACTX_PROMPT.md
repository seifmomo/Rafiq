# IMPACT X Hackathon — RAFIQ Strategy File

Two parts:
1. **PART A — Claude Prompt** (copy everything from here into Claude to regenerate the deck/strategy).
2. **PART B — Executed Strategy** (the full 7-task game plan, already done for RAFIQ).

---

## PART A — COPY THIS INTO CLAUDE

You are the official IMPACT X Hackathon Strategy Coach. Our team of 5 is competing to win by scoring maximum points on the official rubric (total 110 points).

### TEAM MEMBERS & WORKLOAD HIERARCHY (MOST WORK TO LEAST)
1. SEIF (ME) - 40% workload - Lead Developer, Technical Architect, Core Builder, Google Integration, Demo Creator, Impact Measurer
2. JANA - 25% workload - Team Leader, Impact Strategist, Presentation Flow, SDG Alignment, Problem Definition
3. HANA - 15% workload - UX/Design Lead, User Research, Accessibility, User Journey, Visual Design
4. NOUR - 12% workload - Business Analyst, ROI Model, Market Research, Funding Strategy
5. MALAK - 8% workload - Sustainability Lead, Post-Hackathon Roadmap, Scalability Plan, Q&A Prep

### JUDGING CRITERIA
1. Problem Definition & Relevance (10 pts)
2. Impact & Track/SDG Alignment (15 pts) ← HIGHEST PRIORITY
3. Innovation & Creativity (10 pts)
4. Solution Quality & User Experience (10 pts)
5. Technical Implementation (15 pts) ← HIGHEST PRIORITY
6. Feasibility & Execution (10 pts)
7. ROI & Business Model (15 pts) ← HIGHEST PRIORITY
8. Sustainability & Continuity (10 pts)
9. Presentation & Communication (5 pts)
BONUS: Google Technology Integration (+5 pts)
BONUS: Presentation Efficiency (+5 pts)

### OUR PROJECT IDEA (FILLED IN FOR RAFIQ)

**Problem:** ~1.3 billion people globally (1 in 6) live with a disability; Egypt alone has 10-15 million people with disabilities (2021 CAPMAS). People with disabilities in Egypt face two linked crises: (1) no reliable way to get trained, vetted human assistance on demand — families juggle care themselves or trust unvetted helpers; (2) everyday independence tools (navigation, sign communication, medication reminders, instant assistance) are fragmented, costly, or absent. Booking help means phone calls, unknown strangers, and non-negotiable scheduling.

**Solution:** RAFIQ (Arabic for "companion") — an AI-powered accessibility marketplace for the MENA region. One Android app that combines:
- **Assistant Booking Engine (built & working):** user states their disability need (wheelchair transfer, vision assistance, sign-language interpretation, mobility escort, elderly care) → sets from/to and budget → AI matches with a trained assistant (8 sample assistants seeded; scoring = skill match → rating → distance; budget relaxation built in) → gets priced estimate with arrival time → confirms booking with transparency.
- **On-device Sign Language Recognition (built & working):** 10 Arabic/MENA sign gestures recognized 100% offline via Google MediaPipe on the device camera — privacy-first (nothing leaves the phone). Won't miss a beat without internet.
- **AI Voice/Text Assistant with navigation fallback:** route help, accessibility-aware guidance; working OpenRouter LLM integration with an offline fallback so the assistant never goes silent.
- **Screen-reader-friendly UI, EN/AR/FR localization, medication reminders, SOS fallback.**

**Target users:** wheelchair users, people with vision or hearing disabilities, elderly, and their families; plus trained assistants seeking flexible work.

**Current progress:** Production-quality Android app in Kotlin (Jetpack Compose, Hilt, Room, MediaPipe, CameraX, osmdroid, ViewModel state machine). Assistant booking flow complete end-to-end (Form → Results → Summary → Confirmed), 18/18 unit tests passing, lint clean, builds a debug APK. Git repo public (github.com/seifmomo/Rafiq). README includes full business model (10 revenue streams), money flow, and investor pitch.

**Tech stack ideas:** Kotlin, Jetpack Compose, Hilt DI, Room (local), MediaPipe + CameraX (on-device gesture ML), OpenRouter LLM API w/ deterministic offline fallback, google-maps-ready mapping layer (currently osmdroid, **swap to Google Maps SDK for +5 bonus**), Firebase (Auth/ML Kit optional).

**Google Integration for the +5 bonus (RECOMMENDED):**
- **Primary:** Google Maps Platform (Directions + Places) for the booking engine's arrival-time estimates and wheelchair-aware route planning — put Google at the center of the demo.
- **Primary:** Google MediaPipe (already shipped, on-device sign recognition — genuinely Google, demonstrable offline).
- **Secondary:** Gemini API to power the AI assistant replies (replace OpenRouter), Firebase Analytics/Crashlytics for the KPI dashboard.

### TASKS
Run Tasks 1-7 below and output: (1) validated problem with evidence, (2) full task assignment by workload hierarchy, (3) under-8-minute pitch structure with timing, (4) top-10 hardest Q&A with model answers and assigned speakers, (5) 110-point scorecard tracker with estimated scores, (6) week-by-week execution timeline, (7) 3-paragraph winning strategy. Also generate the full slide-by-slide deck content (title, problem, why it matters, solution, live demo, technology, impact+ROI, continuity, closing) with speaker notes.

---

## PART B — EXECUTED STRATEGY FOR RAFIQ

### TASK 1: VALIDATE OUR PROBLEM & PROJECT

**Q1. "Would this problem still matter if the hackathon did not exist?" → PASS (with evidence to prove)**
This is a permanent structural problem, not a hackathon-borrowed trend.
- WHO: 1.3B people (16% of world population) live with disability; disabled women and elderly skew higher.
- Egypt: CAPMAS 2021 estimate of 10-15M persons with disabilities; Egypt's aging population grows senior care demand.
- No mainstream Arabic accessibility marketplace exists — existing apps (Be My Eyes, Care.com) are English-first, unreliable regionally, and don't offer vetted on-demand trained assistants.
**Evidence to show:** a stat slide ("1 in 6" + Egypt CAPMAS figure + "no Arabic competitor"), plus 3 user quotes from Hana's research interviews.

**Q2. "Who benefits, how much, and how will the team prove the impact?" → PASS
- Beneficiaries: (a) PWD — independence + safety + dignity; (b) families — freed from caregiving burden; (c) assistants — flexible income.
- KPIs (demo-integrated): booking setup time reduced from hours/days (phone-call) to **under 2 minutes**; assistant arrival estimate; match quality (skill match %); offline gesture recognition accuracy (98%+); sign-language access 24/7.
- Proof: live demo at judging + on-device accuracy readout + seeded assistant dashboard showing matched, priced bookings.

**Q3. "Who pays or funds it, why, what does it cost, and what measurable value is created?" → PASS (strengthen with numbers)**
- Payers: users (per-booking service fee), assistants (small commission), families (premium subscriptions), corporate/government accessibility programs (B2B contracts), advertisers/NGOs.
- 10 revenue streams already documented in README (booking commission, premium plans, certification fees, B2B corporate accessibility, AI assistant subscription, anonymized insights, API licensing, ads, NGO/government partnerships, SDK licensing).
- Measurable value: assistant income per booking, bookings/month, platform GMV, time saved per user.
- Fix to apply: Nour builds a simple unit-economics table — e.g., 120 EGP avg booking, 15% commission = 18 EGP/booking; target 1,000 bookings/mo year 1 = 18K EGP/mo gross.

**Q4. "If funded tomorrow, could this realistically move toward launch?" → PASS WITH GAPS (fix these)**
- Strengths: working app, working booking engine, offline ML, clear roadmap.
- Gaps (name them honestly + fix): (1) assistant supply is seeded/simulated, not a real onboarding pipeline — fix with "become an assistant" registration + Google ID verification; (2) no real payment gateway — swap to a demoable integration (e.g., Paymob test mode or Google Pay for Test); (3) map layer — swap osmdroid → Google Maps SDK (also wins bonus); (4) live backend beyond local Room — document a roadmap to Firebase/Firestore for synced listings.

**Q5. "What happens after IMPACT X ends?" → PASS WITH PLAN**
- 90-day post-hackathon: pilot with 1 NGO partner + 50 users + 15 vetted assistants in one Cairo district; collect KPI data; formalize assistant certification; publish pilot report.
- Funding asks: Google for Startups / Injaz Accelerator / AUC V-Lab, Misr El Kheir accessibility program, Sawiris Foundation grants.
- Team continuity: Seif owns tech, Malak publishes sustainability roadmap; monthly demo milestone.

### TASK 2: ASSIGN ALL TASKS BY WORKLOAD HIERARCHY

**SEIF (40%)**
- Technical Implementation (15): finalize stack; swap osmdroid→Google Maps SDK; swap OpenRouter→Gemini API (or keep OpenRouter + justify); build "become an assistant" flow; add Paymob/Google Pay test-mode checkout; wire real booking persistence to Firebase (or mock API contract).
- Google Bonus (+5): implement Google Maps Directions ETA inside booking summary + Gemini replies; demo MediaPipe offline sign recognition; wire Firebase Analytics events for KPIs.
- Solution Quality (10): confirm booking→confirm→email-style confirmation covers the core user need; document privacy (on-device ML, consent screen).
- Impact measurement: instrument events (booking_started, booking_confirmed, gesture_used, assistant_contacted) → dashboard numbers for demo.
- Innovation (10): 1-page "why this is novel" (Arabic-first accessibility marketplace + on-device sign AI + booking engine) vs Be My Eyes/Care.com/speed-dial apps.
- Feasibility (10): technical risk register (map API quota, Gemini latency, offline fallback) with mitigations; 4-week roadmap.
- Demo: rehearse the 30-45s core demo until flawless; prepare offline-safe demo fallback.

**JANA (25%)**
- Problem Definition (10): final 1-sentence problem statement; evidence pack (WHO+CAPMAS+quote); write story of "Mahmoud, wheelchair user, needed help at 6pm Tuesday."
- Impact & SDG (15): map to **SDG 8 (Decent Work)**, **SDG 3 (Health)**, **SDG 10 (Reduced Inequalities)**, **SDG 11 (Sustainable Cities)**; define impact KPIs slide (reach, time-saved, income, independence).
- Presentation & Communication (5): magnetize the 8-minute flow; handshake open + "one number" close.
- Team coordination: rubric checklist meeting ×2/week; owns the 110-point scorecard.

**HANA (15%)**
- UX & Solution Quality (10): user journey map (book a helper in 6 taps); accessibility audit (contrast, TalkBack, min touch targets); demo the app on high-contrast + large font.
- Innovation (10, shared): competitor teardown (Be My Eyes, Care.com, Kawtar apps) → "we combine + go offline."
- Research (support Jana): 3 persona cards (wheelchair, vision, elderly family) + interview evidence.
- Visual storytelling: KPI dashboard mock + slide visual theme (clean, warm, accessible).

**NOUR (12%)**
- ROI & Business Model (15): unit economics table; 10 revenue streams summary; funding "ask" paragraph; market sizing (Egypt 10-15M PWD + caregivers).
- Feasibility (10, support Seif): adoption path (NGO pilot → district → city); MVP costs; pricing assumptions.
- Funding strategy: shortlist grants/accelerators with criteria + deadlines.

**MALAK (8%)**
- Sustainability & Continuity (10): post-hackathon roadmap (3 phases); maintenance plan; partner map (NGOs, hospitals, universities); next milestone (pilot launch date).
- Presentation (support Jana): demo script + Q&A flashcards; timekeeper role (watch the 8-minute clock).
- Feasibility (support Nour): long-term operating model and team continuity plan.

### TASK 3: 10-MINUTE PITCH STRUCTURE (TARGET: 7:45 → +5 BONUS)

| Section | Time | Speaker | Key Message | Visual/Demo |
|---|---|---|---|---|
| Hook + Problem | 1:00 | Jana | "Mahmoud needs help at 6pm — he has 0 options." | 1-sentence problem on screen + photo |
| Why It Matters | 1:00 | Jana | 1.3B people worldwide; 10-15M in Egypt; no Arabic option | Stats slide (WHO+CAPMAS) |
| Solution | 0:45 | Seif | "RAFIQ = AI-powered accessibility marketplace." | Product screenshot |
| Live Demo | 1:30 | Seif | Need → match → price → confirmed in under 1 min | Live app: book wheelchair assistant |
| Technology + Google | 1:00 | Seif | Compose + Hilt + MediaPipe offline sign AI + Google Maps ETA + Gemini | Architecture diagram + offline gesture demo |
| Impact + ROI | 1:00 | Nour | Book in <2 min not days; 18 EGP/booking at 15%; 1,000 bookings/mo | KPI + unit-economics slide |
| Continuity | 0:45 | Malak | 90-day pilot with NGO, then district scale | Roadmap timeline graphic |
| Closing | 0:45 | Jana | "1 in 6 people. One app for their independence." | Branded close + QR to repo/app |
| TOTAL | 7:45 | — | — | Buffer 2:15 for Q&A |

### TASK 4: TOP 10 HARDEST Q&A

1. **"How is this different from Be My Eyes?" (Innovation 10)** → Answer: Be My Eyes is live video only, English-first, no trained assistant booking, no offline sign AI. RAFIQ = marketplace + on-device Arabic sign recognition + booking engine with transparent pricing. Owner: Seif.
2. **"Prove your impact — what does 'worth it' look like?" (Impact 15)** → Answer: induce booking time from ~3 hrs to <2 min; sign access available even offline; assistants gain income; we show on-device accuracy + demo metrics live. Owner: Jana.
3. **"Who pays and why?" (ROI 15)** → Answer: 15% commission from a real booking (18 EGP avg) + premium families + B2B accessibility contracts; social value from restored independence. Owner: Nour.
4. **"What if you have no assistants at launch?" (Feasibility 10)** → Answer: seed supply via "become an assistant" onboarding + NGO partner lists; marketplace needs supply-side marketing; pilot starts at 15 vetted assistants. Owner: Malak.
5. **"Your map ETA is Google — quota costs?" (Technical 15)** → Answer: Google Maps free-tier quota covers pilot; we cache routes; fallback to our own algorithm if quota exceeded (already have osmdroid code). Owner: Seif.
6. **"Is on-device ML accurate for Arabic sign?" (Technical 15)** → Answer: 10 core gestures, 98%+ offline accuracy, expandable model, no latency, privacy-preserving. Owner: Seif.
7. **"Why marketplace and not just a booking form?" (Innovation 10)** → Answer: matching + pricing + trust with transparent profiles = what makes assistance safe and repeatable. Owner: Seif.
8. **"What happens after you win?" (Sustainability 10)** → Answer: 90-day NGO pilot, certification program, accelerator application, publish KPI report. Owner: Malak.
9. **"Costs and revenue that are credible?" (ROI 15)** → Answer: unit-economics table with real Egypt pricing and conservative adoption. Owner: Nour.
10. **"Is this accessible to non-tech users / elderly families?" (UX 10)** → Answer: TalkBack, high contrast, large font, 3-tap booking, AR/EN/FR; Hana built for the caregiver too. Owner: Hana.

### TASK 5: SCORE CARD TRACKER

| Criterion | Max | Est Now | Must Do To Reach Max | Evidence in Presentation | Owner |
|---|---|---|---|---|---|
| Problem Definition & Relevance | 10 | 7 | Add Egypt CAPMAS stat + 1 user quote | Problem slide | Jana |
| Impact & SDG Alignment | 15 | 9 | SDG mapping slide + 5 KPIs with numbers | Impact slide + demo metrics | Jana |
| Innovation & Creativity | 10 | 8 | 1-line differentiator vs Be My Eyes/Care.com | Solution + tech slides | Seif |
| Solution Quality & UX | 10 | 8 | Hana UX audit pass + accessibility settings demo | App screenshot + journey map | Hana |
| Technical Implementation | 15 | 11 | Google Maps SDK + Gemini; Firebase events; clean repo | Architecture diagram + live demo | Seif |
| Feasibility & Execution | 10 | 7 | Risk log + 4-week roadmap slide | Technology slide | Seif |
| ROI & Business Model | 15 | 9 | Unit-economics table + funding ask | Impact+ROI slide | Nour |
| Sustainability & Continuity | 10 | 7 | 3-phase post-hackathon plan | Continuity slide | Malak |
| Presentation & Communication | 5 | 3 | Rehearse to 8-min, strong hook | Full pitch | Jana |
| BONUS: Google Integration | 5 | 2 | Ship Google Maps SDK + Gemini + MediaPipe story | Demo | Seif |
| BONUS: Presentation Efficiency | 5 | 2 | Sub-8-min run, polished transitions | Timing | Malak |
| **TOTAL** | **110** | **73** | — | — | — |

### TASK 6: WEEK-BY-WEEK EXECUTION TIMELINE

**Week 1 — Problem validation, research, stack lock:** Jana finalizes problem statement + evidence (Mon–Tue); Hana runs 3 user interviews + personas (Mon–Fri); Seif locks stack + swaps osmdroid→Google Maps SDK (Wed–Sun); Nour drafts unit economics (Sat–Sun); Malak lists partners/grants (Fri–Sun).
**Week 2 — Prototype + Google integration + design:** Seif builds assistant onboarding + Google Maps ETA in booking summary + Gemini replies + Firebase events (all week); Hana designs slide visual theme + KPI dashboard mock (Tue–Sun); Jana writes SDG mapping + impact KPIs (Mon–Thu); Nour completes business model one-pager (Sat–Sun); Malak drafts pilot plan (Sun).
**Week 3 — Full build, testing, business polish:** Seif wires Paymob/Google-Pay test checkout + regression test (18/18 green) (Mon–Fri); Hana accessibility audit + contrast fixes (Mon–Thu); Nour finalizes funding ask + market size (Fri–Sun); Jana rehearses story draft v1 (Sun); Malak finalizes 3-phase roadmap.
**Week 4 — Polish + presentation prep:** Seif frozen demo (new code frozen Tue), offline-safe demo run (Wed); Jana+Hana final deck + visuals (Mon–Thu); Malak Q&A flashcards + timekeeping (Mon–Fri); team full 8-min rehearsals Thu–Fri; scorecard review Friday.

### TASK 7: WINNING STRATEGY SUMMARY

**Paragraph 1 (Impact — 15 pts + Problem 10):** RAFIQ wins on IMPACT because it solves a problem that existed long before this hackathon and will exist after: 1.3 billion people worldwide — 10-15 million in Egypt — need trusted on-demand human assistance and have no Arabic-first option. Our score is earned by evidence (WHO, CAPMAS, user interviews) and by measurable KPIs baked into the product itself: a booking that once took hours of phone calls is completed in under two minutes, matched by skill, transparently priced, with 24/7 sign access even offline. Judges see impact not as a promise but as live data in the demo.

**Paragraph 2 (Technical + Google Bonus — 15 + 5):** RAFIQ is not a pitch-deck prototype; it is a working, test-passing Android application. We win the implementation criterion with a production-quality stack — Kotlin, Jetpack Compose, Hilt, Room, ViewModel state machine, 18/18 unit tests, lint-clean build — and a genuinely central Google story: MediaPipe runs sign-language recognition on-device, Google Maps SDK powers real arrival-time estimates in the booking engine, and Gemini powers the assistant. Google is not a logo tacked onto a slide; it is the engine of our core demo, offline by design.

**Paragraph 3 (ROI + Sustainability — 15 + 10):** RAFIQ closes the deal on business and continuity. Nour's unit economics convert social need into a credible model — 15% commission on a real booking, premium family plans, B2B accessibility contracts, 10 documented revenue streams — while Malak's plan turns the hackathon exit into a 90-day NGO pilot, an assistant-certification program, and an accelerator application. We answer "what happens after IMPACT X" with a dated roadmap, scored KPIs, and a team whose workload is already distributed (Seif 40 / Jana 25 / Hana 15 / Nour 12 / Malak 8) to survive beyond the weekend.

---

### DECK GENERATION INSTRUCTIONS FOR CLAUDE (append after PART A)

Using the strategy above, generate the complete slide deck as structured markdown in this order (one H1 per slide, speaker notes under each):
1. Title — RAFIQ: The AI Accessibility Marketplace
2. The Problem (Jana)
3. Why It Matters (evidence: WHO/CAPMAS)
4. Meet the User (persona)
5. The Solution (Seif)
6. Live Demo (30-45s script)
7. Technology + Google Integration (diagram text)
8. Impact + SDG Alignment (KPIs)
9. Business Model & ROI (unit economics)
10. Feasibility & Risk
11. Sustainability & Roadmap (Malak)
12. The Ask & Closing (Jana)

For each slide give: exact on-slide text (max 20 words), one visual note, and 2-3 spoken lines (8-min pace). Also output the top-10 Q&A as printable flashcards.