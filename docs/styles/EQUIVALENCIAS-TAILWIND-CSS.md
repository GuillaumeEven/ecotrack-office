# Equivalencias Tailwind → CSS Variables

Documento que mapea la configuración Tailwind comentada en `src/index.html` (líneas 51-153) a las nuevas variables CSS del sistema de diseño Kinetic Enterprise.

## Tabla de Equivalencias por Categoría

### 🎨 Colores (Colors)

Toda la paleta de colores Tailwind comentada está directamente mapeada a variables CSS en `variables.css`:

| Tailwind Config | Variable CSS | Valor |
|---|---|---|
| `primary` | `--primary` | `#000000` (Slate Navy) |
| `on-primary` | `--on-primary` | `#ffffff` |
| `primary-container` | `--primary-container` | `#131b2e` |
| `on-primary-container` | `--on-primary-container` | `#7c839b` |
| `primary-fixed` | `--primary-fixed` | `#dae2fd` |
| `primary-fixed-dim` | `--primary-fixed-dim` | `#bec6e0` |
| `on-primary-fixed` | `--on-primary-fixed` | `#131b2e` |
| `on-primary-fixed-variant` | `--on-primary-fixed-variant` | `#3f465c` |
| **Secondary** | | |
| `secondary` | `--secondary` | `#006a61` (Energetic Teal) |
| `on-secondary` | `--on-secondary` | `#ffffff` |
| `secondary-container` | `--secondary-container` | `#86f2e4` |
| `on-secondary-container` | `--on-secondary-container` | `#006f66` |
| `secondary-fixed` | `--secondary-fixed` | `#89f5e7` |
| `secondary-fixed-dim` | `--secondary-fixed-dim` | `#6bd8cb` |
| `on-secondary-fixed` | `--on-secondary-fixed` | `#00201d` |
| `on-secondary-fixed-variant` | `--on-secondary-fixed-variant` | `#005049` |
| **Tertiary** | | |
| `tertiary` | `--tertiary` | `#000000` |
| `on-tertiary` | `--on-tertiary` | `#ffffff` |
| `tertiary-container` | `--tertiary-container` | `#001a42` |
| `on-tertiary-container` | `--on-tertiary-container` | `#3980f4` |
| `tertiary-fixed` | `--tertiary-fixed` | `#d8e2ff` |
| `tertiary-fixed-dim` | `--tertiary-fixed-dim` | `#adc6ff` |
| `on-tertiary-fixed` | `--on-tertiary-fixed` | `#001a42` |
| `on-tertiary-fixed-variant` | `--on-tertiary-fixed-variant` | `#004395` |
| **Error** | | |
| `error` | `--error` | `#ba1a1a` |
| `on-error` | `--on-error` | `#ffffff` |
| `error-container` | `--error-container` | `#ffdad6` |
| `on-error-container` | `--on-error-container` | `#93000a` |
| **Surface** | | |
| `surface` | `--surface` | `#f8f9ff` |
| `on-surface` | `--on-surface` | `#0b1c30` |
| `surface-dim` | `--surface-dim` | `#cbdbf5` |
| `surface-bright` | `--surface-bright` | `#f8f9ff` |
| `surface-container-lowest` | `--surface-container-lowest` | `#ffffff` |
| `surface-container-low` | `--surface-container-low` | `#eff4ff` |
| `surface-container` | `--surface-container` | `#e5eeff` |
| `surface-container-high` | `--surface-container-high` | `#dce9ff` |
| `surface-container-highest` | `--surface-container-highest` | `#d3e4fe` |
| `surface-variant` | `--surface-variant` | `#d3e4fe` |
| `on-surface-variant` | `--on-surface-variant` | `#45464d` |
| **Background & Inverse** | | |
| `background` | `--background` | `#f8f9ff` |
| `on-background` | `--on-background` | `#0b1c30` |
| `inverse-surface` | `--inverse-surface` | `#213145` |
| `inverse-on-surface` | `--inverse-on-surface` | `#eaf1ff` |
| `inverse-primary` | `--inverse-primary` | `#bec6e0` |
| **Outline & Borders** | | |
| `outline` | `--outline` | `#76777d` |
| `outline-variant` | `--outline-variant` | `#c6c6cd` |
| `surface-tint` | `--surface-tint` | `#565e74` |

**Uso en CSS:**
```css
.component {
  color: var(--primary);
  background: var(--surface);
  border-color: var(--outline);
}
```

---

### 📐 Espaciados (Spacing)

| Tailwind Config | Variable CSS | Valor | Descripción |
|---|---|---|---|
| `base` | `--spacing-base` | `4px` | Unidad base del grid |
| `xs` | `--spacing-xs` | `8px` | Extra pequeño |
| `sm` | `--spacing-sm` | `16px` | Pequeño |
| `md` | `--spacing-md` | `24px` | Medio |
| `lg` | `--spacing-lg` | `32px` | Large |
| `xl` | `--spacing-xl` | `48px` | Extra large |
| `2xl` | `--spacing-2xl` | `64px` | 2x large |
| `gutter` | `--spacing-gutter` | `24px` | Gutter lateral (igual a md) |
| `container-max` | `--container-max` | `1440px` | Ancho máximo del contenedor |

**Uso en CSS:**
```css
.container {
  max-width: var(--container-max);
  padding-left: var(--spacing-gutter);
  padding-right: var(--spacing-gutter);
  gap: var(--spacing-md);
}
```

---

### 🔷 Border Radius

**Importante:** La configuración Tailwind comentada tiene valores en `rem` que difieren de la convención de "full" (pill shape).

| Tailwind Config | Valor (rem) | Valor (px) | Variable CSS | Equivalente |
|---|---|---|---|---|
| `DEFAULT` | `0.125rem` | `2px` | `--radius-sm` | Esquinas muy pequeñas |
| `lg` | `0.25rem` | `4px` | `--radius-md` | Esquinas pequeñas |
| `xl` | `0.5rem` | `8px` | `--radius-lg` | Esquinas medianas (cards) |
| `full` | `0.75rem` | `12px` | `--radius-xl` | ⚠️ Nota: No es realmente "full" |

**Mapeo CSS:**
```css
/* Tailwind DEFAULT */
.border-default {
  border-radius: var(--radius-sm); /* 2px */
}

/* Tailwind lg */
.border-lg {
  border-radius: var(--radius-md); /* 4px */
}

/* Tailwind xl */
.border-xl {
  border-radius: var(--radius-lg); /* 8px */
}

/* Tailwind full (no es realmente "full", es 12px) */
.border-full {
  border-radius: var(--radius-xl); /* 12px */
}

/* Para forma realmente redondeada/pill (que falta en Tailwind) */
.border-pill {
  border-radius: var(--radius-full); /* 9999px */
}
```

⚠️ **Nota importante:** La configuración Tailwind comentada define `full: '0.75rem'` (12px), que **NO es una forma "full"**. En el nuevo sistema CSS, tenemos `--radius-full: 9999px` para formas realmente redondeadas/pill, que es la convención moderna correcta.

---

### 🔤 Tipografía (Font Family)

Todos los valores de `fontFamily` en Tailwind apuntan a **Inter**:

| Tailwind Config | Variable CSS | Familia Actual |
|---|---|---|
| `headline-md` | `--font-family-base` | `'Inter', -apple-system, ...` |
| `headline-lg-mobile` | `--font-family-base` | `'Inter', -apple-system, ...` |
| `body-md` | `--font-family-base` | `'Inter', -apple-system, ...` |
| `headline-lg` | `--font-family-base` | `'Inter', -apple-system, ...` |
| `title-md` | `--font-family-base` | `'Inter', -apple-system, ...` |
| `body-lg` | `--font-family-base` | `'Inter', -apple-system, ...` |
| `display-lg` | `--font-family-base` | `'Inter', -apple-system, ...` |
| `label-sm` | `--font-family-base` | `'Inter', -apple-system, ...` |

**Uso en CSS:**
```css
body {
  font-family: var(--font-family-base);
}
```

---

### 📏 Tamaños de Fuente (Font Size)

La configuración Tailwind define cada `fontSize` con `[size, { lineHeight, fontWeight }]`. En el nuevo sistema, usamos variables de tipografía completas (`--font-*`):

| Tailwind Config | Tamaño | LineHeight | FontWeight | Variable CSS | Shorthand CSS |
|---|---|---|---|---|---|
| `display-lg` | 48px | 56px | 700 | `--font-display-lg` | `font: var(--font-display-lg)` |
| `headline-lg` | 32px | 40px | 600 | `--font-headline-lg` | `font: var(--font-headline-lg)` |
| `headline-lg-mobile` | 24px | 32px | 600 | `--font-headline-md` | `font: var(--font-headline-md)` |
| `headline-md` | 24px | 32px | 600 | `--font-headline-md` | `font: var(--font-headline-md)` |
| `title-md` | 18px | 24px | 500 | `--font-title-md` | `font: var(--font-title-md)` |
| `body-lg` | 16px | 24px | 400 | `--font-body-lg` | `font: var(--font-body-lg)` |
| `body-md` | 14px | 20px | 400 | `--font-body-md` | `font: var(--font-body-md)` |
| `label-sm` | 12px | 16px | 600 | `--font-label-sm` | `font: var(--font-label-sm)` |

**Uso en CSS (antes con Tailwind):**
```html
<h1 class="text-display-lg">Título Principal</h1>
<p class="text-body-md">Párrafo normal</p>
```

**Uso en CSS (ahora con variables):**
```css
h1 {
  font: var(--font-display-lg);
  letter-spacing: -0.02em;
}

p {
  font: var(--font-body-md);
}

label {
  font: var(--font-label-sm);
  letter-spacing: 0.05em;
}
```

---

## 📝 Ejemplos de Migración

### Ejemplo 1: Button (antes Tailwind, ahora CSS)

**Antes (Tailwind):**
```html
<button class="text-label-sm font-semibold px-md py-sm bg-primary text-on-primary rounded-lg hover:bg-primary-container">
  Presionar
</button>
```

**Ahora (CSS Variables):**
```html
<button class="btn btn-primary">
  Presionar
</button>
```

```css
.btn {
  font: var(--font-label-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--radius-lg);
  border: none;
  cursor: pointer;
  transition: var(--transition-normal);
}

.btn-primary {
  background-color: var(--primary);
  color: var(--on-primary);
}

.btn-primary:hover {
  background-color: var(--primary-container);
}
```

### Ejemplo 2: Card (antes Tailwind, ahora CSS)

**Antes (Tailwind):**
```html
<div class="p-md rounded-xl bg-surface-container border border-outline">
  <h2 class="text-headline-md mb-sm">Título</h2>
  <p class="text-body-md">Contenido</p>
</div>
```

**Ahora (CSS Variables):**
```html
<div class="card">
  <h2>Título</h2>
  <p>Contenido</p>
</div>
```

```css
.card {
  padding: var(--spacing-md);
  border-radius: var(--radius-lg);
  background-color: var(--surface-container);
  border: var(--border-width-1) solid var(--outline);
  box-shadow: var(--shadow-sm);
}

.card h2 {
  font: var(--font-headline-md);
  margin-bottom: var(--spacing-sm);
}

.card p {
  font: var(--font-body-md);
}
```

---

## ✅ Checklist de Migración

- ✅ Todas las variables CSS definidas en `src/app/styles/variables.css`
- ✅ Todas las clases Tailwind reemplazadas con clases CSS semánticas
- ✅ Configuración Tailwind comentada en `src/index.html` para referencia
- ✅ CSS cargado ANTES del bootstrap de Angular (via `angular.json`)
- ✅ Modo oscuro deshabilitado (comentado en variables.css con TODO)
- ✅ Componentes CSS reutilizables en `components.css`
- ✅ Breakpoints responsive definidos en variables.css
- ✅ Z-index scale centralizado en variables.css

---

## 🔗 Referencias

- **Diseño:** [docs/DESIGN.md](../../docs/DESIGN.md) - Especificación de Kinetic Enterprise
- **CSS Variables:** [variables.css](./variables.css) - 150+ custom properties
- **Componentes:** [components.css](./components.css) - 40+ clases reutilizables
- **Global:** [global.css](./global.css) - Reset y estilos base
- **Configuración Angular:** [angular.json](../../../angular.json) - Carga de estilos

---

**Última actualización:** 2024
**Estado:** ✅ Migración completa de Tailwind a CSS Variables
