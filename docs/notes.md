# Notas misceláneas

## Consideraciones

- Papel de la IA en el proyecto: presentarla desde el inicio podría ser una ventaja.
`
## Preguntas

- Presentación del TFM (Trabajo Fin de Máster): es en grupo
- Cadencia de reuniones: ¿semanal?
- Notificaciones en tiempo real: ¿usar SSE (Server-Sent Events)? La IA sugirió que es la opción más sencilla.
- ¿La app es interna para una empresa o es un SaaS? Es un Saas
- ¿Es posible reservar escritorios de forma permanente? No, maximo una semana de antelación
- ¿Es posible reservar salas completas? no
- ¿Con qué antelación se puede reservar? Una semana

### Respondidas
---
- Uso de la IA: según la primera reunión, se considera una buena práctica.

## Puntos de atención en el documento de arquitectura

Las dos tecnologías que no hemos tratado en clase son:

- Migraciones con Flyway: los cambios en la base de datos son sensibles y el esquema no está versionado en el repositorio Git. Para poder revertir cambios es necesario un gestor de migraciones; actúa como Git para la base de datos. La IA recomendó Flyway.

- Notificaciones: el documento menciona SSE y la API EventSource en Angular. Esto es nuevo para mí y debe aclararse con Manuel.

## Reuniones

- [x] Reunión con Manuel | *16 de abril de 2026*

  Nos reunimos con Manuel, nuestro tutor de proyecto. Pidió una visión más clara de nuestros conocimientos actuales, el contenido del curso y la idea inicial del proyecto. Planeamos compartir los ejercicios completados y el borrador del proyecto e invitarle al repositorio de GitHub para más detalles. Manuel también coincidió en el papel de la IA: útil para documentación y organización del trabajo, pero no para programar mientras estamos en fase de aprendizaje.

- [x] Reunión con Manuel | *21 de abril de 2026*
  Asistentes: Edu, Rai, José Luis y Guillaume.
  Resultados:
  1. Los documentos proporcionados por Guillaume eran demasiado densos y no daban al equipo una visión clara y de alto nivel ni un punto de partida.
  2. Manuel facilitará una herramienta colaborativa (Jira) y los recursos necesarios para comenzar a trabajar.
  3. El equipo se reunirá antes de la siguiente tutoría (jueves 30 de abril de 2026) para avanzar en la definición del proyecto, con especial foco en esbozar las diferentes páginas a implementar.

- [x] Sesión de trabajo | *22 de abril de 2026*
  Asistentes: Rai, José Luis y Guillaume.
  Actividades:
  - Empezamos a esbozar páginas (ver entregables de Rai y la lista de pantallas esbozadas).
  Preguntas y decisiones:
  1. ¿La app se entregará A) a clientes para uso interno, o B) será un SaaS donde nuevos usuarios pueden registrarse, pagar y usar el servicio sin nuestra intervención? Elegimos la opción B y empezamos a diseñar las páginas de aterrizaje/home.
  2. ¿Cómo limitar reservas de escritorios para que un usuario malintencionado no reserve la misma escritorio durante seis meses?
  3. ¿Es posible reservar una sala completa?
  4. ¿Quién resuelve los conflictos de reserva? De momento decidimos permitir que el técnico modifique las reservas desde su cuenta para resolver conflictos.

 - [x] Sesión de trabajo | *30 de abril de 2026*

  Decisiones:

  - MVP (A implementar):
    - Registro: Edu/Guillaume — para inscribirse solo hace falta un código de empresa; más adelante se evaluará mejorarlo.
    - Antelación máxima: se puede reservar con una semana de antelación como máximo (decisión provisional).
    - Valor ecológico: agrupar las reservas al máximo para ahorrar electricidad, climatización, limpieza y agua. Estimación rápida (Edu): 8,2K €/año/sala; cálculo de emisiones de CO₂ pendiente.
    - Política de apertura de plantas/salas: restringir reservas a una planta hasta que esté completa o casi (80%); el sistema abrirá nuevas plantas automáticamente. Aplicable también a salas.
    - Tipos de sala:
      - Puestos de trabajo
      - Reunión
    - Elementos por sala: sillas, escritorios, TV, etc.
    - Precisión de reservas: turno (mañana/tarde). * edit del 2 de junio: las reservas se hacen por día;
    - Interfaz de reserva (mapa): vista con rectángulos que representan plantas y salas; se seleccionan solo las disponibles. Al clicar una sala se muestra el plano con escritorios disponibles/reservadas; tooltip para reservar según turno. Incidencias bloquean la escritorio/sala. El técnico puede abrir/cerrar salas.
    - Estimación del ahorro: requiere que el cliente defina su estructura (plantas, m2 por sala). La estimación se calcula por sala cerrada, usando m2 y 365 días al año.
    - Vistas administrativas: el administrador puede crear plantas, salas y escritorios; implementar vistas "Crear plantas/salas/escritorios" en la cuenta admin.
    - Teletrabajo: añadir un indicador de teletrabajo para valorar ahorro de CO₂.
    - Cancelaciones: gestionadas por técnico — cuestión abierta (notificación al usuario, ¿cambio o cancelación?). Por ahora, no implementado.

  - Suscripción / Planes:
    - Se elimina la vista de planes en el flujo público; la suscripción se gestiona desde la cuenta admin.
  
- [x] Sesión de trabajo | *4 de mayo de 2026*
  - Decisiones:
    - el diagrama de clase ha sido puesto al dia (_bmad-output/planning-artifacts/class-diagram.md)
    - el reparto del trabajo es el siguiente:
      - Rai: CRUD Reservas
      - Jose Luis: CRUD Usuarios
      - Edu: CRUD Incidencias/analytics
      - Guillaume: CRUD assets (plantas, salas, escritorios...)
    - el código debe ser escrito en inglés, y la documentación, el front y los comentarios en el código en español.


- [x] Sesión de trabajo | *2 de junio de 2026*

  #### Endpoint: Login

  - Comportamiento: valida usuario/contraseña y devuelve un token.
  - Persistencia del token: el token se guarda en cache (servidor) y las peticiones posteriores comprueban que el token sigue siendo válido.

  Notas de implementación:

  - Considerar almacenar tokens en una caché con expiración (ej. Redis) para rápida validación.
  - Asegurarse de invalidar tokens al cerrar sesión o ante cambios de credenciales.

  #### Tecnologías / librerías

  - Librería posible para gráficos en Angular: `ng2-charts`.

  #### Producto: visión general

  Estamos desarrollando una aplicación de desk-sharing con las vistas principales:

  - Home
  - Login
  - Registrar
  - ...

  Flujo en la vista Home:

  - La entidad "piso" sustituye a lo que antes llamábamos "salas". Al seleccionar un piso en el mapa se mostrarán las salas pertenecientes a ese piso.
  - Si se selecciona una sala de reuniones: se abre un popup para reservar la sala completa.
  - Si se selecciona una sala de trabajo: el mapa cambia para mostrar las escritorios. Al clicar una escritorio se abre un popup para reservarla.

  Decisión provisional:

  - Sistema de notificaciones: deshabilitado por el momento.

  #### Sesion del 17 de junio

TODO:
Imprescindible:
Hoy creamos las paginas que nos hacen falta (mi component works !), mañana Rai configura los botones de nav
- [ ] paginas:
  - [x] gestión edificio: crud floors/recursos para -> gui
  - [x] gestion usuarios para el admin -> JL
  - [x] editar perfil: operativo -> JL
  - [x] form de inscripción -> Rai
  - [x] limpiar header y hacer botones operativos (quitar lo que es navegacion)-> ??
  - [x] desactivar desk/meeting en caso de incidencia (con mensaje "a la espera de intervencion" ?) -> gui ?
  - [x] Vizualizar errores en front
  - [x] limpiar sidebar y hacer botones operativos (navegacion: home, reservas, incident, empresa(admin)) -> Rai
  - [ ] editar organization -> Rai
  - [ ] incidencias (vinculo desde desk/room dialog ?) -> Edu ?
  - [ ] analytics -> Edu
  - [ ] reservas -> Rai
  - [ ] reformar la arquitectura del front (Rai hace una propuesta)

- [ ] Form registro dinamico:
  - [ ] un formulario comun de user
    - [ ] afiliarte a una empresa
      - [ ] cif
    - [ ] crear una empresa
      - [ ] form creacion empres

En nuestros sueños:
- [ ] generacion de codigo empresa para que admin invite a nueavos usuarios
- [ ] clean code: comentarios, logging, sonarCube...
- [ ] hash passwords (a JL ?)
- [x] Recuperar la estructura de la memoria
- [ ] harmonizar los controladores: poner cualquier respuesta como ResponseEntity
- [ ] desiciones de diseño
- [ ] collection postman
- [ ] añadir docstring para swagguer
- [ ] harmonizar el css
- [ ] docker/deploy
- [x] desactivar desk/meeting en caso de incidencia (con mensaje "a la espera de intervencion" ?) -> gui ?
- [ ] enviar un email de invitacion
- [x] repensar los status de resources: una resource no puede ser reservada o disponible en si misma, depende de las reservas.

Muchas gracias chicos, un placer trabajar con vosotros ! Y esas cosas...

incidentRepository
@Query("SELECT i FROM IncidentEntity i WHERE i.user.organization.id = :organizationId")


Rai: reserva pagina
Buenas chicos aquí os dejo mi propuesta. Fata poner algunas cosas de las tablas al gusto y pensar si se quiere salgan todas las reservas o solo algunas o incluso pensar en poner algún filtro? No se jajaja bueno. He vuelto a tocar bastantes cosas porque tenía que arreglar cositas en el back de reservation. De nuevo he vuelto a apuntar algunas cositas para mi, las dejo aquí para que lo penséis también uwu

    TENGO UNA DUDA CON EL TEMA DE LAS RESERVAS Y ES QUE YO AHORA VOY A CAMBIAR EL RESPONSEDTO PERO NO SE SI ESTO VA ROMPER EL FRONT DEL BUILDINGMAP DE GUILLAUME. ADEMÁS HE VISTO QUE ÉL EN EL REPOSITORY PUEDE HACER UN SELECT HACIENDO REFERENCIA AL FLOORID DEL RESOURCE PERO CLARO EL RESOURCE A VECES SERÁ UNA SALA O UN ESCRITORIO Y ESO ME TIENE DANDO VOLTERETAS PORQUE NO SE COMO FUNCIONA YA QUE ES DIFERENTE Y TAL NO SE.
    ESTOY TENIENDO UN PROBLEMA CON QUITAR EL RESOURCEENTITY DEL RESERVATIONRESPONSEDTO YA QUE NO PUEDO CAMBIARLO POR UN RESOURCERESPONSEDTO PORQUE ESTE ES ABSTRACTO. SUPONGO QUE EL MÉTODO SERÍA PONER EL ID Y SI QUIERO MOSTRAR MÁS CAMPOS QUE PERTENECEN AL RESOURCE PUES PONERLOS DIRECTAMENTE AUNQUE NO SE MUY BIEN COMO SERÍA YA QUE PUEDEN SER MESAS O SALAS Y ADEMÁS ESTARÍA BIEN SABER LA PLANTA.