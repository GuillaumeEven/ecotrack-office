# Arquitectura de Estilos

## Estructura

```
src/app/styles/
├── variables.css    # Propiedades CSS personalizadas (colores, espaciados, tipografía, etc.)
├── global.css       # Reset, fundaciones, tipografías globales
├── components.css   # Estilos reutilizables (botones, tarjetas, modales, etc.)
└── README.md        # Esta documentación
```

## Importación

Los estilos se importan en `src/main.ts` **en este orden** :
1. `variables.css` → Define las variables
2. `global.css` → Utiliza las variables para reset & fundaciones
3. `components.css` → Utiliza variables & global para los componentes

## Principios

### 1️⃣ Variables CSS para todo lo que se repite

No copiar/pegar colores, espaciados, etc. Utilizar las variables :

```css
/* ❌ Malo */
.mi-boton {
  padding: 8px 16px;
  background: #0066cc;
  border-radius: 8px;
}

/* ✅ Bien */
.mi-boton {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--primary);
  border-radius: var(--radius-md);
}
```

### 2️⃣ Estilos globales en `components.css`

Si un estilo es utilizado por **múltiples componentes**, va en `components.css` :

```css
/* components.css */
.btn { /* compartido */ }
.card { /* compartido */ }
```

Ejemplo de uso :
```html
<!-- En cualquier plantilla -->
<button class="btn btn-primary">Hacer clic</button>
<div class="card">Contenido</div>
```

### 3️⃣ Estilos específicos en el componente

Si un estilo concierne **sólo a un componente**, permanece **encapsulado** en ese componente :

```typescript
// building-map.component.ts
@Component({
  selector: 'app-building-map',
  templateUrl: './building-map.component.html',
  styleUrls: ['./building-map.component.css']  // ← Encapsulado
})
export class BuildingMapComponent {}
```

```css
/* building-map.component.css */
.desks-cinema {
  /* Estilo ESPECÍFICO a este componente */
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  gap: var(--spacing-md);
}
```

### 4️⃣ Cuándo usar la encapsulación de Angular

Angular aísla automáticamente los estilos con `ViewEncapsulation.Emulated` (predeterminado) :

```css
/* building-map.component.css */
.desks-cinema { /* Este universo .desks-cinema se aplica SÓLO aquí */ }
h2 { /* Este h2 se aplica SÓLO en este componente */ }
```

Esto evita conflictos. Utiliza esta encapsulación para :
- Los diseños complejos específicos
- Las animaciones locales
- Los estilos de estados del componente

## Paleta de Colores

```css
--primary: #0066cc;           /* Acciones principales */
--secondary: #10b981;         /* Acciones secundarias */
--danger: #ef4444;            /* Destructivas, errores */
--warning: #f59e0b;           /* Atención */
--success: #10b981;           /* Confirmaciones */

--status-available: #10b981;  /* Recurso libre */
--status-occupied: #f59e0b;   /* Recurso ocupado */
--status-reserved: #ef4444;   /* Reservado */
--status-unavailable: #999999;/* No disponible */
```

## Espaciados

Escala de 4px (utilizado en todas partes) :

```css
--spacing-xs:  4px;    /* Espacios micro */
--spacing-sm:  8px;    /* Espacios pequeños */
--spacing-md: 16px;    /* Estándar */
--spacing-lg: 24px;    /* Espacios grandes */
--spacing-xl: 32px;    /* Muy grandes */
--spacing-2xl: 48px;   /* Enormes */
```

Uso:
```css
.card {
  padding: var(--spacing-lg);        /* 24px */
  margin-bottom: var(--spacing-md);  /* 16px */
}
```

## Componentes Reutilizables

### Botones

```html
<!-- Variantes -->
<button class="btn btn-primary">Primario</button>
<button class="btn">Secundario</button>
<button class="btn btn-danger">Peligro</button>
<button class="btn btn-outline">Contorno</button>
<button class="btn btn-ghost">Fantasma</button>

<!-- Tamaños -->
<button class="btn btn-primary btn-sm">Pequeño</button>
<button class="btn btn-primary">Normal</button>
<button class="btn btn-primary btn-lg">Grande</button>
```

### Tarjetas

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

### Formularios

```html
<div class="form-group">
  <label class="form-label required">Nombre</label>
  <input type="text" class="form-input" />
  <p class="form-help">Este campo es obligatorio</p>
</div>
```

### Insignias & Estado

```html
<!-- Insignias -->
<span class="badge badge-primary">Primario</span>
<span class="badge badge-success">Éxito</span>
<span class="badge badge-danger">Peligro</span>

<!-- Insignias de Estado -->
<span class="status-badge available"></span>
<span class="status-badge occupied"></span>
<span class="status-badge reserved"></span>
```

## Migración desde Tailwind → CSS Puro

### Antes (Tailwind)
```html
<button class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition">
  Hacer clic
</button>
```

### Después (CSS Puro)
```html
<button class="btn btn-primary">Hacer clic</button>
```

```css
/* components.css - ya definido */
.btn {
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--primary);
  border-radius: var(--radius-md);
  transition: all var(--transition-fast);
}

.btn-primary:hover {
  background: var(--primary-dark);
}
```

## Puntos de Corte

```css
--breakpoint-sm: 640px;  /* Móvil */
--breakpoint-md: 1024px; /* Tableta */
--breakpoint-lg: 1280px; /* Escritorio */
```

Uso:
```css
@media (min-width: var(--breakpoint-md)) {
  .mi-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
```

## Animaciones

```css
--transition-fast:   150ms ease-in-out;
--transition-normal: 300ms ease-in-out;
--transition-slow:   500ms ease-in-out;
```

Uso:
```css
.btn {
  transition: all var(--transition-fast);
}

a {
  transition: color var(--transition-fast);
}
```

## Escala Z-Index

```css
--z-dropdown:  100;
--z-sticky:    200;
--z-fixed:     300;
--z-modal:     1000;
--z-popover:   1100;
--z-tooltip:   1200;
```

Para evitar conflictos de apilamiento, utilizar siempre las variables :

```css
/* ✅ Bien */
.modal {
  z-index: var(--z-modal);
}

.tooltip {
  z-index: var(--z-tooltip);
}
```

## Accesibilidad

- Siempre proporcionar `:focus-visible` para navegación con teclado
- Utilizar `.sr-only` para textos invisibles pero accesibles para lectores de pantalla
- Verificar el contraste de los colores (WCAG AA mínimo)

## Consejos & Buenas Prácticas

1. **Antes de añadir una clase CSS**, verificar si ya existe en `components.css`
2. **Utilizar las variables** en lugar de valores hardcodeados
3. **Responsive primero** : móvil → tableta → escritorio
4. **Sin estilos en línea** : todo en los archivos CSS
5. **DRY (Don't Repeat Yourself)** : si copias CSS, va en `components.css`

---

**¿Preguntas?** 👉 ¡Pide al equipo o mejora esta documentación! 📚
