# Miscellaneous notes

## Considerations

- Role of AI within the project: presenting it upfront could be an advantage.

## Questions

- Presentation of the TFM (Final Master's Project): individual or group?
- Meeting cadence: weekly?
- Use of AI: according to the first meeting, it is considered a good practice.
- Real-time notifications: should we use Server-Sent Events (SSE)? The AI suggested it is the simplest approach.

## Points of attention in the architecture document

The two technologies we have not covered in class are:

- Flyway migrations: making changes to the database is sensitive, and the database schema is not versioned in the Git repository. To be able to roll back changes, a migration tool is necessary; it acts like Git for database changes. The AI recommended Flyway.

- Notifications: the document mentions SSE and the EventSource API in Angular. This is new to me and should be clarified with Manuel.

## Meetings

- [x] Meeting with Manuel | *16 April 2026*

  We met Manuel, our project tutor. He requested a clearer overview of our current knowledge, the course content, and our initial project idea. We plan to share our completed exercises and the project draft and invite him to the GitHub repository for more details. Manuel also agreed with our stance on the role of AI: useful for documentation and organizing work, but not to be used for coding while we are still in a learning phase.
