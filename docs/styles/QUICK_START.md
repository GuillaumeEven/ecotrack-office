# 🎨 Arquitectura CSS - Inicio Rápido

## TL;DR — 30 seg

1. **Estilos globales** (botones, tarjetas, modales) → **utiliza las clases en `components.css`**
2. **Estilos específicos** del componente → **mantenlos en `.component.css`**
3. **Siempre usar variables CSS** (`--primary`, `--spacing-md`, etc.)

## Estructura

```
src/app/styles/
├── variables.css       ← Colores, espaciados, tipografía, sombras
├── global.css          ← Reset, body, fundaciones
└── components.css      ← Botones, tarjetas, modales, etc.
```

## Ejemplos Rápidos

### ✅ Botones

```html
<!-- Utiliza las clases globales -->
<button class="btn btn-primary">Hacer clic</button>
<button class="btn">Secundario</button>
<button class="btn btn-danger btn-sm">Pequeño Peligro</button>
<button class="btn btn-outline">Contorno</button>
```

### ✅ Tarjetas

```html
<div class="card">
  <div class="card-header">
    <h3>Título</h3>
  </div>
  <div class="card-body">Contenido</div>
  <div class="card-footer">
    <button class="btn btn-primary">Acción</button>
  </div>
</div>
```

### ✅ Formularios

```html
<div class="form-group">
  <label class="form-label required">Email</label>
  <input type="email" class="form-input" />
  <p class="form-help">Formato: usuario@ejemplo.com</p>
</div>
```

### ✅ Usa las Variables

```css
/* ✅ BIEN */
.mi-componente {
  padding: var(--spacing-md);
  color: var(--primary);
  transition: all var(--transition-fast);
}

/* ❌ MALO */
.mi-componente {
  padding: 16px;
  color: #0066cc;
  transition: all 300ms;
}
```

### ✅ Estilos Específicos del Componente

Si se utiliza **sólo aquí**, está en el archivo del componente :

```typescript
// mi-feature.component.ts
@Component({
  selector: 'app-mi-feature',
  templateUrl: './mi-feature.component.html',
  styleUrls: ['./mi-feature.component.css']  // ← ENCAPSULADO
})
export class MiFeatureComponent {}
```

```css
/* mi-feature.component.css */
.mesas-cine {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: var(--spacing-md);
}

@media (max-width: 640px) {
  .mesas-cine {
    grid-template-columns: repeat(4, 1fr);
  }
}
```

## Paleta de Colores

```css
--primary: #0066cc;       /* Acciones */
--secondary: #10b981;     /* Alt acciones */
--danger: #ef4444;        /* Destructivo */
--warning: #f59e0b;       /* Atención */
--success: #10b981;       /* OK */

/* Estados Específicos */
--status-available: #10b981;
--status-occupied: #f59e0b;
--status-reserved: #ef4444;
--status-unavailable: #999;
```

## Espaciados

```css
--spacing-xs:  4px;    /* Micro */
--spacing-sm:  8px;    /* Pequeño */
--spacing-md: 16px;    /* Estándar ← el más utilizado */
--spacing-lg: 24px;    /* Grande */
--spacing-xl: 32px;    /* Muy grande */
```

## Puntos de Corte

```css
@media (min-width: var(--breakpoint-md)) {  /* 1024px */
  /* Tableta + Escritorio */
}

@media (max-width: var(--breakpoint-sm)) {  /* 640px */
  /* Móvil */
}
```

## Sin Tailwind

Adiós a las clases Tailwind :

```html
<!-- ❌ NO -->
<div class="flex items-center justify-between px-4 py-2 bg-gray-100 rounded-lg">

<!-- ✅ SÍ -->
<div class="mi-contenedor">
  <!-- estilos en .component.css -->
</div>
```

## ¿Necesitas Ayuda?

- **Documentación completa** → `src/app/styles/README.md`
- **Ejemplo de migración** → `src/app/styles/MIGRATION_EXAMPLE.md`
- **Componentes disponibles** → ver `src/app/styles/components.css`

---

**Recuerda:** ¡Antes de añadir una clase, verifica si ya existe en `components.css`! 🎯
