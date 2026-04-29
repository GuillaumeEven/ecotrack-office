# Miscellaneous notes

## Considerations

- Role of AI within the project: presenting it upfront could be an advantage.

## Questions

- Presentation of the TFM (Final Master's Project): individual or group?
- Meeting cadence: weekly?
- Real-time notifications: should we use Server-Sent Events (SSE)? The AI suggested it is the simplest approach.
- use of bruno testing
- assign role to each people ?
- la app es interna a una empresa, o es un sas ?
- como se crea la primera cuenta, la del empresario
- es posible reservar mesas de forma permanente ?
- es posible reservar salas enteras ?
- con que antelación se puede reservar ?

### Answered
---
- Use of AI: according to the first meeting, it is considered a good practice.

## Points of attention in the architecture document

The two technologies we have not covered in class are:

- Flyway migrations: making changes to the database is sensitive, and the database schema is not versioned in the Git repository. To be able to roll back changes, a migration tool is necessary; it acts like Git for database changes. The AI recommended Flyway.

- Notifications: the document mentions SSE and the EventSource API in Angular. This is new to me and should be clarified with Manuel.

## Meetings

- [x] Meeting with Manuel | *16 April 2026*

  We met Manuel, our project tutor. He requested a clearer overview of our current knowledge, the course content, and our initial project idea. We plan to share our completed exercises and the project draft and invite him to the GitHub repository for more details. Manuel also agreed with our stance on the role of AI: useful for documentation and organizing work, but not to be used for coding while we are still in a learning phase.

- [x] Meeting with Manuel | *21 April 2026*
  Attendees: Edu, Rai, José Luis, and Guillaume.
  Outcomes:
  1. The documents provided by Guillaume were too dense and did not give the team a clear, high-level view of the project or indicate where to start.
  2. Manuel will provide a collaborative work tool (a Jira) and the necessary resources to begin working.
  3. The team will meet before the next tutor meeting (Thursday, 30 April 2026) to advance the project definition, focusing in particular on sketching the different pages to be implemented.

- [x] Work session | *22 April 2026*
  Attendees: Rai, José Luis, and Guillaume.
  Activities:
  - Began sketching pages (see Rai's deliverables and the list of pages sketched).
  Questions and decisions:
  1. Is the app intended A) to be delivered to clients for internal use, or B) a SaaS where new users can sign up, pay, and use the service without our intervention? We chose option B and started designing the landing/home pages.
  2. How can we limit table reservations so that a malicious user cannot reserve the same table for the next six months?
  3. Is it possible to reserve an entire room?
  4. Who resolves booking conflicts? For now we decided to allow the technician to modify everyone’s reservations from their account to resolve conflicts.