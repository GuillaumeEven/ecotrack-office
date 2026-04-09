# PROJECT: EcoTrack Office

**Smart Workspace & Sustainability Management System**

## 1. Introduction and Vision

EcoTrack Office is a "Smart Office" solution. In today's hybrid work context, companies need to know who will be in the office, optimize lighting and HVAC usage, and comply with new European sustainability regulations.

This project transforms a simple reservation manager into an engineering tool that saves the company money and reduces its environmental impact.

## 2. Why this is a strong proposal (T-Systems context)

Multinationals like T-Systems (part of Deutsche Telekom) do not look for isolated applications; they look for data ecosystems.

- **Alignment with ESG:** Large companies must report CO₂ savings; this app would provide that data.
- **Scalability:** Designed to manage anything from a small office to a 10-floor building.
- **Professional stack:** We'll use industry-standard technologies (Java/Spring + Angular/React), combining academic best practices with real-world tooling.

## 3. Detailed Features (Usage Examples)

### A. Interactive Map & Digital Twin
- **Context:** Users select from a visual map rather than a list.
- **Example:** José Luis sees Floor 1 plan; free desks are green. Clicking a desk shows details (dual monitor, near window, etc.).

### B. Reservation Engine with Check-in
- **Context:** Avoid no-shows (ghost desks).
- **Example:** If Rai reserves for 09:00 but doesn't confirm by 09:15, the desk is automatically released for others.

### C. Real-time Incident System
- **Context:** If a resource fails, it must be excluded from booking.
- **Example:** Eduardo reports Room A's projector as faulty; the room is automatically blocked until fixed.

### D. Energy Savings Algorithm
- **Context:** Concentrate occupants to save energy.
- **Example:** If only five people come on a Friday, the system suggests they use Zone A so lights and HVAC in Zones B and C can be turned off.

## 4. Technical Breakdown (4 Work Areas)
Each area will implement a full CRUD (Create, Read, Update, Delete) set.

### Block 1 — User Architecture & Security
- **Responsibility:** Employee registration, permission levels (admins), and secure login.
- Procedural and structured.

### Block 2 — Physical Asset Management & Inventory
- **Responsibility:** Office database: rooms, desks, equipment, and the visual model of the office.
- Highly visual and crucial for first impressions.

### Block 3 — Transactional Logic & Validations
- **Responsibility:** Prevent double bookings, manage schedules, automatic cancellations, calendars.
- Requires robust backend logic.

### Block 4 — Intelligence & Operations (Analytics)
- **Responsibility:** Dashboard: aggregate data to display CO₂ savings, occupancy metrics, and incident handling.
- Mix of business logic and data visualization.

---

**Note:** As a differentiator, we could develop the entire project in English (documentation and code) if the team is comfortable; the final presentation could remain in Spanish if preferred.

## 5. Technical Stack

The proposed implementation stack for the project:

- **Front-end:** AngularJS (for a fast, component-based SPA). Use `AngularJS 1.x` if explicitly required, otherwise prefer modern Angular (v12+) for long-term support.
- **Back-end:** Spring Boot (Java) with Maven or Gradle for build and dependency management. Expose RESTful APIs and use Spring Security for authentication/authorization.
- **Database:** MySQL for transactional data (users, reservations, assets).
- **Authentication:** JWT-based stateless auth for the API.

This stack provides a robust enterprise-ready baseline while remaining familiar to students and maintainable for the long term.