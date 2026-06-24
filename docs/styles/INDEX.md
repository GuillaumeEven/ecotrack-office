# Carpeta Estilos - Índice

## 📚 Documentación

| Archivo | Descripción | Público |
|---------|-------------|---------|
| **QUICK_START.md** | Guía ultra-rápida (30s) | ⭐ COMIENZA AQUÍ |
| **README.md** | Doc completa & detallada | 📖 Referencia |
| **MIGRATION_EXAMPLE.md** | Ejemplos de migración | 🔄 Para migrar componentes |
| **variables.css** | Variables CSS globales | 💻 Código |
| **global.css** | Reset & fundaciones | 💻 Código |
| **components.css** | Componentes reutilizables | 💻 Código |

---

## 🎯 Por Caso de Uso

### Debo añadir una nueva feature
1. Leer **QUICK_START.md** (2 min)
2. Utilizar las clases `.btn`, `.card`, `.form-input` de `components.css`
3. Poner tus estilos específicos en `mi-componente.component.css`

### Debo migrar un componente existente
1. Leer **MIGRATION_EXAMPLE.md**
2. Reemplazar las clases Tailwind por nombres específicos
3. Utilizar variables CSS

### Debo personalizar los colores
1. Ir a `variables.css`
2. Buscar `--primary`, `--secondary`, etc.
3. Cambiar los valores (¡eso lo actualiza en todas partes!)

### Tengo una pregunta sobre CSS
1. Verificar **README.md** (sección Componentes Reutilizables)
2. Ver los ejemplos en `components.css`

---

## 🔧 Configuración

**Importación en `src/main.ts`:**
```typescript
import './app/styles/variables.css';
import './app/styles/global.css';
import './app/styles/components.css';
```

**Config de Angular (`angular.json`):**
```json
"styles": []  // Los imports se hacen via main.ts
```

---

## 📊 Arquitectura

```
src/app/
├── core/
├── shared/
├── layouts/
├── features/
│   └── mi-feature/
│       ├── mi-feature.component.ts
│       ├── mi-feature.component.html
│       └── mi-feature.component.css  ← Estilos específicos ENCAPSULADOS
│
└── styles/                           ← ESTÁS AQUÍ
    ├── variables.css    ← Variables compartidas
    ├── global.css       ← Fundaciones globales
    └── components.css   ← Componentes reutilizables
```

---

## ✅ Lista de Verificación de Migración (para el equipo)

- [ ] Crear `.component.css` para cada componente (si no existe)
- [ ] Reemplazar las clases Tailwind por nombres orientados al negocio
- [ ] Utilizar variables CSS (nunca valores hardcodeados)
- [ ] Utilizar las clases globales `.btn`, `.card`, etc.
- [ ] Probar la encapsulación CSS (solo estilos locales)
- [ ] Validar responsividad
- [ ] Validar accesibilidad (focus-visible)

---

## 🎨 Colores Principales

| Var | Color | Uso |
|-----|-------|-----|
| `--primary` | #0066cc | Acciones principales |
| `--secondary` | #10b981 | Acciones secundarias |
| `--danger` | #ef4444 | Acciones destructivas |
| `--warning` | #f59e0b | Atención |
| `--success` | #10b981 | Confirmaciones |

---

## 📏 Espaciados (Escala 4px)

| Var | Valor | Uso |
|-----|-------|-----|
| `--spacing-xs` | 4px | Espacios micro |
| `--spacing-sm` | 8px | Espacios pequeños |
| `--spacing-md` | 16px | **Estándar (más común)** |
| `--spacing-lg` | 24px | Espacios grandes |
| `--spacing-xl` | 32px | Muy grandes |
| `--spacing-2xl` | 48px | Enormes |

---

## 🚀 Enlaces Rápidos

- [Variables CSS disponibles](./variables.css)
- [Componentes globales](./components.css)
- [Ejemplo: Migración de un componente](./MIGRATION_EXAMPLE.md)
- [Arquia doc (propuesta-arch-front.md)](../../docs/propuesta-arch-front.md)

---

**Última Actualización:** 24 de Junio de 2026
**Estado:** ✅ Listo para Producción
