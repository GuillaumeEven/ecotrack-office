EcoTrack Office — Clickable Prototype (low-fidelity)

How to open
1. Open `_bmad-output/implementation-artifacts/prototype/index.html` in your browser.

Quick interactions
- Click any colored circle (desk) on the SVG map to open the booking drawer.
- Click "Reserve" to simulate a reservation (marker becomes orange).
- Simulate a magic-link check-in by opening the file with `?checkin=desk-4` appended to the URL (for example: `file://.../index.html?checkin=desk-4`). This will show the check-in confirmation and mark the desk as occupied.

Notes
- This is a static prototype with placeholder SVG geometry for quick UX validation. No backend.
- If you want, I can: export a Figma file, add realistic SVG floor assets, or convert this to a tiny static server for sharing.
 - Standalone SVG exports are available in `prototype/svg/`:
	 - `floor3-mainwing.svg` — full floor plan (rooms, zone overlays, desks, legend)
	 - `desk-icon.svg` — single desk icon (rectangle + monitor)
	 - `legend.svg` — legend panel as a separate asset
 - These files are ready for Figma import or developer asset pipelines.
