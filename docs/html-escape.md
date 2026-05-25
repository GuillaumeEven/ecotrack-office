# Resumen sobre HTML escaping y buenas prácticas (Spring + Angular)

## ¿Por qué hacer HTML escaping?
El HTML escaping se utiliza para prevenir inyecciones HTML y vulnerabilidades XSS (Cross‑Site Scripting). Si se inserta texto no confiable directamente en una plantilla HTML, un atacante puede inyectar etiquetas o scripts que el navegador ejecutará. Escapar convierte caracteres especiales en entidades HTML (por ejemplo `<` → `&lt;`, `>` → `&gt;`, `&` → `&amp;`, `ó` → `&oacute;`) para que el contenido se trate como texto y no como código ejecutable.

## Diferencia entre escapado y no escapado (ejemplo)
- Texto no escapado: mantiene los caracteres originales. Ejemplo: `López`.
- Texto escapado: reemplaza caracteres por entidades HTML. Ejemplo: `López` → `L&oacute;pez`.

En nuestra aplicación el recorrido fue:
1. El frontend envía `López` en la petición `PUT /api/v1/users/me`.
2. `UserMeController` construye `UserMeRequestDto` y llama a `UserService.updateMe(...)`.
3. `UserService.updateMe` actualmente hace `HtmlUtils.htmlEscape(dto.lastName())` antes de guardar.
4. La entidad se guarda en la base de datos con la cadena escapada (`L&oacute;pez`).
5. `UserModel.fromEntity(...)` copia ese valor y la API responde con `L&oacute;pez`.

Consecuencia: los clientes y tests esperan `López` pero reciben `L&oacute;pez`, lo que provoca fallos y datos almacenados en forma no deseada.

## Buenas prácticas para un stack Spring (API) + Angular (frontend)
- Las API REST deben almacenar y devolver texto UTF‑8 "crudo"; no aplicar `htmlEscape` antes de persistir.
- Dejar que Angular haga el escaping al renderizar:
  - La interpolación `{{ value }}` en Angular ya escapa automáticamente y protege contra XSS.
  - Evitar `[innerHTML]` salvo necesidad; si se usa, sanitizar con `DomSanitizer` y aceptar solo HTML permitido.
- Para campos JSON (por ejemplo `preferencesJson`): validar que sea JSON válido y almacenarlo como JSON o texto sin aplicar `htmlEscape`.
- Si el backend recibe HTML intencional (WYSIWYG), filtrar/sanitizar el HTML (por ejemplo con una librería de sanitización) en vez de convertirlo en entidades.
- Si ya hay datos escapados en la base, planificar una migración o aplicar `HtmlUtils.htmlUnescape(...)` al leer mientras se corrige el origen.

## Recomendaciones concretas
- Quitar `HtmlUtils.htmlEscape` en `UserService.updateMe` para `firstName` y `lastName`.
- No aplicar `htmlEscape` a `preferencesJson`; validar/almacenar como JSON.
- Realizar una migración de datos si existen valores escapados en la DB (o usar `HtmlUtils.htmlUnescape` temporalmente al leer).
- Escapar/filtrar únicamente en el punto de renderizado HTML (frontend) o al generar vistas HTML en servidor.