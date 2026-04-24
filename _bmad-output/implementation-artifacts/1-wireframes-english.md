# EcoTrack Office — Wireframes (English)

This document contains low-fidelity wireframes for the EcoTrack Office MVP. Each screen includes: purpose, primary elements, and a compact ASCII-style wireframe for quick reference.

---

## 1. Home / Floor Map (Map-first)
Purpose: Main landing screen where users see building floor(s) and available desks.
Primary elements:
- Top bar: logo, search, user avatar
- Left: floor selector + filter chips (amenities)
- Main: interactive SVG floor map with desk markers and zone heat overlay
- Floating CTA: "Quick Book" (opens last-used preferences)
- Bottom thin status bar: real-time sync indicator

Wireframe (desktop):

+------------------------------------------------------------+
| Topbar: EcoTrack         [Search]            [User Avatar]  |
+------------------------------------------------------------+
| Sidebar (280px) |                Floor map (svg)           |
| - Floor selector |  [SVG map area with markers & overlays] |
| - Filter chips   |  [Desk marker] [Desk marker] [Zone]     |
|                 |  [Hover tooltip -> bottom drawer open]  |
+------------------------------------------------------------+
| Status: Live sync • 30s polling •  [Quick Book] [My Bookings] |
+------------------------------------------------------------+

Notes: Desk markers color-coded: green (available), orange (reserved), red (occupied), grey (unavailable). Hover shows quick info; click opens booking drawer.

---

## 2. Signup / Onboarding (includes GDPR consent)
Purpose: Register a new user and collect GDPR consent.
Primary elements:
- Form: name, email, password
- Privacy checkbox + link to Privacy Policy
- Optional: organization code
- CTA: "Create account and accept privacy policy"

Wireframe:

+-----------------------------+
| Sign up                    |
| Name [__________]          |
| Email [__________]         |
| Password [__________]      |
| [ ] I accept the Privacy Policy (link) |
| [Create account]           |
+-----------------------------+

Notes: Consent text short; link opens full policy. Optionally present "Continue with magic link" for low-friction signups.

---

## 3. Login / Magic Link
Purpose: Let users authenticate; provide magic-link option for reluctant users.
Primary elements:
- Email input + "Send magic link"
- Password login (optional)
- Links: Sign up, Need help

Wireframe:

+-----------------------------+
| Login                      |
| Email [__________] [Send link]
| — Or —
| Email [__] Password [__] [Sign in]
+-----------------------------+

Notes: Magic link leads to stateless check-in flow; no session required for check-in via email link.

---

## 4. Desk Detail + Booking Drawer (Bottom Sheet)
Purpose: Detail a desk and confirm booking with one action.
Primary elements:
- Desk info: name, zone, amenities, map thumbnail
- Date selector (prefilled), time slot (prefilled)
- Reserve CTA and small note field

Wireframe:

+-----------------------------------------------+
| [Desk 4B] • Zone A • Monitor • Window | [X]    |
| Date: [Today v]  Time: [09:00–18:00 v]       |
| Amenities: [Monitor] [Window] [Standing]     |
| [Optional note ________]                     |
| [Reserve — primary CTA]  [Cancel]            |
+-----------------------------------------------+

Notes: On Reserve: optimistic UI updates marker to orange; snackbar confirms success. On conflict, show nearby alternatives.

---

## 5. Check-in Flow (QR + Email Link)
Purpose: Confirm physical presence; supports QR scan and magic-email link.
Primary elements:
- Confirmation screen with large check mark and message
- If QR scan: token validated → success
- If expired: show option to extend or rebook

Wireframe:

+-----------------------------+
| ✓ Checked in                |
| Desk 4B — Floor 3           |
| Thank you — enjoy your day   |
| [Return to map]             |
+-----------------------------+

Notes: Email link must be stateless. If token expired, show rebook flow.

---

## 6. Incident Report (User flow)
Purpose: Quickly report a broken resource with photo.
Primary elements:
- Short description, attach photo, select affected desk/room
- Submit button; UI immediately blocks resource from booking

Wireframe:

+--------------------------------+
| Report an issue                 |
| Select resource [Desk 4B v]     |
| Description: [___________]      |
| [Attach photo] [Submit]         |
+--------------------------------+

Notes: On submit: technician notified; resource marked unavailable until resolved.

---

## 7. Admin Dashboard (José Luis)
Purpose: High-level occupancy overview and consolidation suggestion.
Primary elements:
- Top KPIs: occupancy %, ghost-desk rate, open incidents
- Compact zone map or small heatmap
- Action buttons: Approve consolidation, Export report

Wireframe:

+------------------------------------------------+
| Topbar: Admin • [Export] [Settings]            |
+------------------------------------------------+
| KPI1: Occupancy 78%  | KPI2: Ghost rate 3%     |
| KPI3: Open incidents: 2 | [Consolidation suggestion banner]
+------------------------------------------------+
| Mini map (zones) | Recent incidents list       |
+------------------------------------------------+

Notes: Dashboard must be scannable in 3 seconds; consolidation suggestion requires one click to apply.

---

## Accessibility & Responsive Notes
- Map-first on desktop; table/list fallback for low-bandwidth or assistive needs.
- Color should never be the sole indicator of status; always use icons and labels in non-map UIs.
- Keyboard navigation: focusable desk markers; bottom sheet reachable via keyboard; skip nav provided.

---

## Deliverables & Next Steps
- This file: low-fidelity wireframes in English (this document).
- Next: create higher-fidelity mockups (Figma/Sketch) and export assets on request.
- Option: I can also produce a clickable HTML prototype (SVG placeholders) if you want.

---

Generated by `bmad-agent-quick-flow-solo-dev` — wireframes saved to project output.
