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

Edu/Gui: para inscribirse, solo hace falta un codigo de empresa, luego veremos si nos da tiempo a mejorarlo
Se puede reservar con una semana de antelacion como maximo
Valor eco: unir las reservas el maximo posible: ahorro de luz, climatization, limpieza y agua. pero como medir ese beneficio: se ahorra los gastos de una planta, por ejemplo. Calculo volando de Edu: 8.2K/año/sala + el beneficio ecologica, en termas de carbono (calculo por hacer)
Otro sistema ? -> las reservas estan restringidas a una planta hasta que este completa o casi (80%), el sistema abre nuevas plantas automaticamente. Y eso a la escala de salas tambien
(llega Jose luis)
Dos tipos de sala:
  - puestos de trabajo
  - reunion
Elementos: Sillas, mesas, tv...
Precision de las reservas: no por hora, sino por turno mañana/tarde
Mapa: un rectangulo con rectangulos representando salas, solo se puede pinchar las disponibles a la reserva. Pinchas en una sala, aparece el mapa de esta sala con mesas disponibles y reservadas. al pasar encima de una mesa libre se abre un tooltip para reservar mañana o tarde segun disponibilidad. Si una mesa lleva una incidencia no se puede reservar, igual con las salas. El tecinco puede abrir o cerrar una sala a la reserva.
Como estimamos el beneficio ?
Alguien de la empresa cliente debe diseñar su empresa.
El admin puede crear sala. Tenemos que programar las vistas 'crear plantas/salas/mesas' acciessible en la cuenta administrador.