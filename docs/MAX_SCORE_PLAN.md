# RAFIQ — MAX SCORE ACTION PLAN (73 → 110)

> How to close every gap on the IMPACT X rubric. Estimated current **73/110**. Target **110/110**.
> Owner-verified with the team; each line is a specific, doable task before Judging Day.

---

## HOW TO READ THIS
- **Gap** = points currently left on the table.
- **Action** = the exact thing that earns them. Order matters: do the HIGH-first rows.
- **Checked** = done in the repo already.

---

## CRITERION-BY-CRITERION

### 1. Problem Definition & Relevance (10) — est. 7 → 10
| Action | Repo state |
|---|---|
| Add `docs/PROJECT_DETAILS.md` problem section (WHO 1-in-6, Egypt CAPMAS 10–15M) | ✅ done |
| Prepare 3 real user quotes (Hana interviews) — one each: wheelchair, vision, family caregiver | ⬜ to collect |
| One-line problem statement on slide 2 and in the hook | ✅ deck has it |

### 2. Impact & Track/SDG Alignment (15) — est. 9 → 15
| Action | Repo state |
|---|---|
| 4 SDGs on one slide: SDG 3, 8, 10, 11 with a sentence each | ✅ in PROJECT_DETAILS |
| 5 impact KPIs with baseline→target numbers | ⬜ add numbers column to slide 14 |
| Show **live demo metrics** during the pitch (booking time <2 min, match %, gesture accuracy) | ⬜ rehearse |

### 3. Innovation & Creativity (10) — est. 8 → 10
| Action | Repo state |
|---|---|
| Differentiator line: "Arabic-first accessibility marketplace + on-device sign AI + booking engine" | ✅ deck slide 5 |
| Competitor teardown slide (Be My Eyes = video only; Care.com = listing) | ✅ deck slide 11 |
| Show on-device offline gesture demo live | ⬜ rehearse |

### 4. Solution Quality & User Experience (10) — est. 8 → 10
| Action | Repo state |
|---|---|
| Hana accessibility audit checklist (contrast, TalkBack, 48dp targets, AR/FR) | ⬜ doc + fix pass |
| Demo settings: dark mode, large font, Arabic | ⬜ 30s in demo |
| Booking in ≤6 taps claim — time it live | ⬜ rehearse |

### 5. Technical Implementation (15) — est. 11 → 15
| Action | Repo state |
|---|---|
| 18/18 unit tests passing, lint clean, APK builds | ✅ verified |
| **Google Gemini API wired** (AIza keys work now; default gemini-2.5-flash) | ✅ CODE DONE |
| Google Maps SDK swap (osmdroid → google-maps) for ETA + wheelchair routes | ⬜ high-risk; see bonus below |
| "Become an assistant" onboarding + Paymob/Google Pay test mode + Firebase sync | ⬜ stretch |
| Live demo of booking → match → confirm (30–45s) | ⬜ rehearse |

### 6. Feasibility & Execution (10) — est. 7 → 10
| Action | Repo state |
|---|---|
| 4-week roadmap slide with owners + deadlines | ⚠️ in CLAUDE_IMPACTX (Week tables) |
| Technical risk register (map quota, Gemini latency, offline fallback) with mitigations | ⬜ add to PROJECT_DETAILS |
| Pitch "we know our gaps, here is the fix order" honestly | ⬜ practice |

### 7. ROI & Business Model (15) — est. 9 → 15
| Action | Repo state |
|---|---|
| 10 revenue streams slide | ✅ deck slide 9 |
| **Unit economics table** (avg booking 120 EGP × 15% = 18 EGP/booking × 1,000/mo = 18K EGP/mo) | ✅ in PROJECT_DETAILS; add slide |
| Fundable "ask" paragraph (how much, for what, metrics to repay it) | ⬜ one paragraph |
| Pricing assumptions + 3-year adoption curve | ⬜ Nour |

### 8. Sustainability & Continuity (10) — est. 7 → 10
| Action | Repo state |
|---|---|
| 3-phase post-hackathon roadmap (pilot → scale → MENA) | ✅ deck slide 15 |
| Dated next milestone (pilot launch date) | ⬜ set a date |
| Partner map: NGO (Misr El Kheir), hospitals, universities, accelerators | ⬜ Malak |

### 9. Presentation & Communication (5) — est. 3 → 5
| Action | Repo state |
|---|---|
| 7:45 total (under 8-min bonus) with assigned speakers | ✅ structure in CLAUDE_IMPACTX |
| Strong hook + "one number" close, rehearsed 3× aloud | ⬜ rehearse |

---

## BONUSES

### BONUS A: Google Technology Integration (+5) — est. 2 → 5
Current shipped Google tech in the product:
1. **Google MediaPipe** — on-device sign-language recognition (10 gestures, offline, private). Already runs. **This is the central integration.**
2. **Google Gemini API** — AI assistant replies (just wired; `GEMINI_API_KEY=AIza...` works, default model `gemini-2.5-flash`). ✅ CODE DONE
3. **Firebase** — messaging + analytics (optional `google-services.json`).
4. **Google Maps deep links** — "Open in Google Maps" SOS + navigation.

**To close the +5:**
- [ ] Demo sign language with a **zero-signal phone** ("Google runs on-device — private").
- [ ] Show a Gemini reply in chat ("Google's model answers accessibility questions").
- [ ] Story slide: "Google powers the product: MediaPipe, Gemini, Firebase, Maps."
- [ ] (Optional, higher risk) Swap osmdroid → **Google Maps SDK** so the booking ETA is Google's Directions API. Only attempt if you can test on a device with an API key; otherwise keep the deep-link story.

### BONUS B: Presentation Efficiency (+5) — est. 2 → 5
- [ ] Run all sections once with a stopwatch; hard-cut anything over 7:45.
- [ ] Demo pre-staged: booking flow starts already at the needs screen (1 tap).
- [ ] Backup demo recording; if a live step fails, switch to clip in <5s.
- [ ] Q&A batons: each judge question is answered by its owner in ≤2 sentences.

---

## PREDICTED RE-SCORE AFTER APPLYING EVERYTHING
Problem 10 · Impact 15 · Innovation 10 · Solution 10 · Technical 15 · Feasibility 10 · ROI 15 · Sustainability 10 · Presentation 5 · **Google +5** · **Efficiency +5** = **110**.

## QUICK WIN BY PRIORITY (this week)
1. **Demo rehearsal** (worth ~10+ pts combined: technical, impact, innovation, UX, efficiency).
2. **Gemini key in `gradle-secrets.properties`** → live AI replies on the day (already supported in code).
3. **Unit-economics slide + fundable ask** (ROI 15).
4. **Impact KPI numbers column** (Impact 15).
5. **Hana accessibility pass + record settings demo** (UX 10).
6. **Set the pilot launch date + partner list** (Sustainability 10).