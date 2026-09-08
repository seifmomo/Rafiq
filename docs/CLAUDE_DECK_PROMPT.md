# RAFIQ — Claude Deck Builder Prompt

> Paste this into **Claude** to generate a self-contained, presentation-ready HTML deck (Claude Artifacts). Present directly from a browser or export to PDF/PPTX. Built around the **emotional impact story** angle (5–7 min, ~9 slides).

---

## THE PROMPT (paste into Claude)

```
Create a complete, self-contained HTML presentation deck (16:9, one slide per
<section> shown full-screen, navigated with arrow keys or click) for RAFIQ — an
AI-powered accessibility companion for people with disabilities. This is for a
5–7 minute live competition pitch.

## BRAND & DESIGN SYSTEM (follow exactly)
- Palette: Teal #14B8A6, Cyan #06B6D4, Dark Navy #0F172A, warm off-white #F8FAFC,
  soft gray text #64748B. White surfaces.
- Aesthetic: premium medical/accessibility, "Samsung Health meets ChatGPT" —
  generous whitespace, large readable type, rounded cards, subtle soft shadows,
  one accent color per section. NO clutter, NO walls of bullet text.
- Typography: big elegant display font for headlines, system sans for body.
- Language: respectful, inclusive, human. Never clinical or pity-based.
- Every slide: 1 headline + minimal supporting text + strong visual or layout.
  Add inline SVG icons/illustrations (no external image dependencies) so the
  deck is fully self-contained.

## SLIDES (9 total)

1. TITLE — RAFIQ — "Your Companion, Every Step."
   Subtitle: "An AI companion giving people with disabilities independence,
   safety, and a voice." Small footer: RAFIQ = Arabic for 'companion / friend'.

2. MEET LAILA — Emotional persona.
   "Meet Laila. She's 24, uses a wheelchair, and lives in Cairo alone."
   One sentence, big image of a young woman (SVG silhouette). Then:
   "Today, getting around is a maze. Making herself heard is exhausting.
   A missed call can mean real danger." → "This is what changes tonight."

3. A DAY WITHOUT vs. A DAY WITH — the empathy split screen.
   LEFT (muted/gray, tone of struggle): missed medication, an
   inaccessible building, panic in an emergency.
   RIGHT (warm, glowing): a reminder fires, the map shows the accessible
   ramp, SOS is sent in 10 seconds.
   Headline: "The same day. A different world."

4. ONE TAP, THREE LIFELINES — the SOS story.
   Visual: a big SOS circular button with a 10s countdown ring.
   Three lifeline cards: (1) SMS to emergency contact, (2) live location
   shared, (3) guardian alert via realtime backend.
   Headline: "Danger. 10 seconds. Three lifelines."

5. THE WOW, IN ONE GESTURE — sign language hero.
   One large SVG hand doing a "Thumbs Up → Yes" gesture, with a subtle
   detection overlay + the word "YES" spoken.
   Caption: "Real-time sign language. On-device. No internet. 100% private."

6. OFFLINE-FIRST BY DESIGN — the unfair advantage.
   "Most accessibility apps fail when the network does. RAFIQ works
   on-device — offline, everywhere."
   Cards: SOS still fires · sign language is offline · AI answer works
   without a key · map + storage on device.

7. BUILT LIKE PRODUCTION — brief engineering strip (4 small items, one line each).
   Android: Kotlin · Jetpack Compose · MVVM + Hilt + Room
   On-device AI: MediaPipe + CameraX
   Backend: Node.js · Express · PostgreSQL · WebSocket · JWT
   Quality: 10/10 unit tests passing · lint clean · live API verified
   Headline: "Polished like a real product, not a school project."

8. IMPACT — outcome-focused, concise.
   "Independence. Safety. A voice. A companion that never leaves."
   Short supporting line about accessibility by design (dark mode, font
   scaling, AR/FR localization, voice in / voice out).

9. CLOSING — "Accessibility isn't a feature. It's the whole product."
   Big, warm, minimal. Footer: "RAFIQ — Your Companion, Every Step."

## TECHNICAL
- Output a single `deck.html` artifact, fully self-contained (all CSS/JS inline,
  no external CDN) so it opens offline and can be exported to PDF/PPTX.
- Keyboard: → / space next, ← previous; show slide counter bottom-right.
- At the very end include a printable notes section (each slide's 1-paragraph
  speaking script) marked clearly for the presenter.
```

---

## AFTER CLAUDE GENERATES IT

1. Review the SCRIPT/notes per slide — make them *yours*, not robotic.
2. Present directly from `deck.html` in a browser (fullscreen, F11). WiFi on but the file runs **offline** anyway.
3. To make a PDF: open in Chrome → Ctrl+P → Save as PDF → "Background graphics" ON.
4. To make PPTX: paste slides into PowerPoint/Gamma, or present the HTML directly (cleanest).
5. **Never skip the live demo** — the deck sets the stage, but the sign-language gesture + SOS countdown are what judges remember.

---

## THINGS NOT TO DO
- ❌ No bullet-spam slides. Max 3-4 short items per slide.
- ❌ No reading tech stack aloud slide-by-slide (it's visual, glance and move on).
- ❌ No overclaiming features that aren't demoable (keep Firebase push light this round; SMS + WebSocket are your reliable paths).
- ❌ Don't make the deck a crutch — 80% of the win is the human demo + story.
