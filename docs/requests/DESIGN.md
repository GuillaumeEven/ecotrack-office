---
name: Kinetic Enterprise
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#45464d'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#006a61'
  on-secondary: '#ffffff'
  secondary-container: '#86f2e4'
  on-secondary-container: '#006f66'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#001a42'
  on-tertiary-container: '#3980f4'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#89f5e7'
  secondary-fixed-dim: '#6bd8cb'
  on-secondary-fixed: '#00201d'
  on-secondary-fixed-variant: '#005049'
  tertiary-fixed: '#d8e2ff'
  tertiary-fixed-dim: '#adc6ff'
  on-tertiary-fixed: '#001a42'
  on-tertiary-fixed-variant: '#004395'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-lg:
    fontFamily: Inter
    fontSize: 48px
    fontWeight: '700'
    lineHeight: 56px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 32px
    fontWeight: '600'
    lineHeight: 40px
    letterSpacing: -0.01em
  headline-lg-mobile:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  headline-md:
    fontFamily: Inter
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-md:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '500'
    lineHeight: 24px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  label-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '600'
    lineHeight: 16px
    letterSpacing: 0.05em
rounded:
  sm: 0.125rem
  DEFAULT: 0.25rem
  md: 0.375rem
  lg: 0.5rem
  xl: 0.75rem
  full: 9999px
spacing:
  base: 4px
  xs: 8px
  sm: 16px
  md: 24px
  lg: 32px
  xl: 48px
  container-max: 1440px
  gutter: 24px
---

## Brand & Style

This design system is engineered for the high-performance modern workplace. It targets facility managers, office administrators, and employees who require a frictionless, reliable interface to manage physical assets and spatial logistics.

The brand personality is **Professional, Efficient, and Intentional**. The UI evokes a sense of calm control through a **Modern Minimalist** aesthetic. It utilizes a structured, card-based layout with significant whitespace to reduce cognitive load in data-dense environments. Precision is prioritized over decoration; every element serves a functional purpose, ensuring the platform feels like a high-end productivity tool rather than a generic utility.

## Colors

The palette is anchored by "Slate Navy" (Primary) and "Corporate Slate" (Neutral) to establish professional authority. "Energetic Teal" serves as the primary accent, specifically used to highlight "Saved Energy" metrics and environmental efficiency, creating a positive psychological link between the platform and sustainable operations.

### Functional States
- **Available:** A vibrant Emerald green, signaling openness and safety.
- **Reserved:** A steady Amber, indicating temporary unavailability.
- **Maintenance:** A sharp Crimson, denoting high-priority issues or "Out of Order" status.
- **Selected:** A bright "Cinema Blue," used for active focus in spatial selectors and room booking.

## Typography

The design system utilizes **Inter** exclusively to leverage its exceptional legibility and systematic feel. The type hierarchy is strictly defined to manage complex information dashboards. 

- **Display & Headlines:** Use tighter letter-spacing and heavier weights to create a strong visual anchor for page titles.
- **Labels:** Small caps and increased letter-spacing are used for metadata and category headers to differentiate them from interactive body text.
- **Body Text:** Standardized at 14px for density without sacrificing readability in data tables and management sidebars.

## Layout & Spacing

The system employs a **12-column fluid grid** for main dashboard views, transitioning to a **fixed-width centered layout** (1440px max) for settings and administrative forms. 

### Rhythm
- **8px Soft Grid:** All component dimensions and internal padding must be multiples of 8px.
- **Margins:** Desktop views maintain a 32px outer margin; mobile views downscale to 16px.
- **Reflow:** On tablet devices, sidebars collapse into a drawer, and 3-column card layouts reflow to a 2-column stack.

## Elevation & Depth

This design system uses **Tonal Layers** combined with **Ambient Shadows** to create a structured hierarchy. Depth is used functionally rather than decoratively.

- **Level 0 (Surface):** The background layer, using a subtle off-white or very light slate.
- **Level 1 (Cards):** Main content containers. They use a white fill and a 1px soft slate border (`#E2E8F0`) with no shadow to maintain a clean "flat" look.
- **Level 2 (Interactive/Hover):** When a card or element is interactive, it gains a "Large" ambient shadow (0px 10px 15px -3px rgba(15, 23, 42, 0.08)) to indicate "lift."
- **Level 3 (Modals/Popovers):** Highest elevation with a deep, diffused shadow to isolate the task from the background.

## Shapes

The shape language is **Soft** and professional. A consistent 4px (0.25rem) radius is applied to small components like buttons and inputs to suggest precision. Larger containers like cards and room selectors utilize an 8px (0.5rem) radius to soften the overall dashboard appearance. Pill shapes are reserved exclusively for status indicators (Available, Reserved, etc.) to distinguish them from actionable buttons.

## Components

### Buttons
- **Primary:** Solid "Slate Navy" with white text. High-contrast.
- **Secondary:** Energetic Teal, used for "Energy Saving" actions or positive confirmations.
- **Ghost:** Transparent background with Slate Navy border for low-priority actions.

### Room Selector (Cinema-Style)
The room/desk selector uses a high-contrast dark background container (`#0F172A`). Individual units (desks/rooms) are squares with status-specific fills:
- **Available:** Subtle teal outline.
- **Selected:** Solid "Cinema Blue" (`#3B82F6`) with a white inner glow.
- **Maintenance:** Stipple pattern or "X" overlay in Crimson.

### Cards
Cards are the primary container. They must have a consistent 24px internal padding. Header sections within cards should have a 1px bottom border to separate titles from content.

### Input Fields
Inputs use a white background, 1px slate border, and a 2px "Cinema Blue" ring on focus. Error states swap the border to Crimson.

### Status Chips
Small, pill-shaped indicators using 10% opacity of the status color for the background and 100% opacity for the text. This ensures accessibility while maintaining the minimalist aesthetic.