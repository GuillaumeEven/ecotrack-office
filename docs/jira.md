# 🔗 Jira + GitHub — Cheatsheet del equipo

> Guía rápida para vincular tu trabajo en Jira con el código en GitHub.

---

## 1. Nombrar ramas correctamente

Incluye siempre la clave del ticket Jira en el nombre de tu rama:

```
git checkout -b PROJ-42-login-con-google
```

> ✅ Jira detecta automáticamente la rama y la muestra en el ticket.

---

## 2. Referenciar tickets en tus commits

Añade la clave del ticket al inicio del mensaje de commit:

```
git commit -m "PROJ-42 Añadir autenticación con Google OAuth"
```

> ✅ El commit aparecerá vinculado al ticket en Jira.

---

## 3. Vincular una Pull Request a un ticket

En el título o descripción de tu PR en GitHub, menciona la clave del ticket:

```
Título: PROJ-42 — Autenticación con Google OAuth

Descripción:
Closes PROJ-42

Cambios:
- Integración con Google OAuth 2.0
- Tests unitarios añadidos
```

> ✅ El ticket se actualizará con el estado de la PR en tiempo real.

---

## 4. Transiciones automáticas de estado

Usa estas palabras clave en tu PR o commit para mover el ticket automáticamente en el kanban:

| Acción en GitHub              | Palabra clave               | Estado en Jira    |
|-------------------------------|-----------------------------|--------------------|
| Abrir una PR                  | *(automático)*              | In Review          |
| Aprobar y mergear la PR       | `Closes`, `Fixes`, `Resolves` + clave | Done         |
| Cerrar PR sin mergear         | *(automático)*              | Vuelve a To Do     |

**Ejemplo en descripción de PR:**
```
Closes PROJ-42
Fixes PROJ-43
```

---

## 5. Ver el código desde Jira

En cualquier ticket Jira, abre el panel lateral derecho:

```
Ticket → Development (panel derecho) → ver Branches / Commits / PRs
```

> ✅ Puedes seguir el avance sin salir de Jira.

---

## 6. Buenas prácticas del equipo

- **Un ticket = una rama** → evita mezclar varios tickets en la misma rama
- **Siempre incluye la clave** en ramas, commits y PRs
- **Usa `Closes PROJ-XX`** en la descripción de la PR para cerrar el ticket al mergear
- **No muevas el ticket manualmente** si tienes las transiciones automáticas activas
- **Revisa el panel Development** en Jira antes de pedir una revisión de código

---

## 7. Referencia rápida de claves

| Clave       | Significado                   |
|-------------|-------------------------------|
| `PROJ-XX`   | Número de tu ticket Jira      |
| `Closes`    | Cierra el ticket al mergear   |
| `Fixes`     | Idem, para bugs               |
| `Resolves`  | Idem, forma alternativa       |

---

*¿Dudas? Pregunta en el canal del equipo o consulta la documentación oficial de la integración Jira + GitHub.* 🚀