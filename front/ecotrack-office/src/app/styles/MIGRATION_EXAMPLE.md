/* ============================================================================
   EJEMPLO DE MIGRACIÓN DE COMPONENTE
   ============================================================================
   Cómo migrar un componente existente de Tailwind/estilos en línea hacia 
   la nueva arquitectura CSS.
   ============================================================================ */

/* ============================================================================
   ANTES (Tailwind / Estilos en línea)
   ============================================================================ */

/*
<div class="px-4 py-6 bg-white rounded-lg shadow">
  <h2 class="text-2xl font-bold mb-4 text-gray-900">Reservas</h2>
  <button class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition">
    Nueva Reserva
  </button>
</div>

PROBLEMAS:
- Clases Tailwind por todas partes
- Estilos mágicos (hardcodeados: px-4, py-6, bg-blue-600)
- Sin variables => cambiar paleta es imposible
- Duplicaciones: px-4 aparece en todas partes
*/

/* ============================================================================
   DESPUÉS (CSS Puro + Encapsulación)
   ============================================================================ */

/*
ESTRUCTURA DEL COMPONENTE:

1) reservation-list.component.ts
2) reservation-list.component.html
3) reservation-list.component.css (ENCAPSULADO)

---

Archivo: src/app/features/reservation/components/reservation-list.component.ts
*/

import { Component } from '@angular/core';

@Component({
  selector: 'app-reservation-list',
  templateUrl: './reservation-list.component.html',
  styleUrls: ['./reservation-list.component.css']  // ← ¡Encapsulado!
})
export class ReservationListComponent {
  reservations = [];

  onCreateReservation() {
    console.log('Crear nueva reserva');
  }
}

/*
---

Archivo: src/app/features/reservation/components/reservation-list.component.html
*/

// ❌ ANTES
/*
<div class="px-4 py-6 bg-white rounded-lg shadow">
  <h2 class="text-2xl font-bold mb-4 text-gray-900">Reservas</h2>
  <button class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition">
    Nueva Reserva
  </button>
</div>
*/

// ✅ DESPUÉS
/*
<div class="reservation-container">
  <h2>Reservas</h2>
  <button class="btn btn-primary" (click)="onCreateReservation()">
    Nueva Reserva
  </button>
</div>
*/

/*
---

Archivo: src/app/features/reservation/components/reservation-list.component.css
*/

/* Contenedor principal */
.reservation-container {
  padding: var(--spacing-lg) var(--spacing-md);
  background-color: var(--surface-high);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

/* Título */
.reservation-container h2 {
  font: var(--font-headline-md);
  margin-bottom: var(--spacing-md);
  color: var(--on-surface);
}

/* El botón utiliza la clase global .btn .btn-primary */
/* ¡Nada que hacer aquí! */

/*
---
RESUMEN: Migración Paso a Paso
*/

/*
PASO 1: Reemplazar las clases Tailwind por clases orientadas al negocio
  px-4 py-6 bg-white rounded-lg shadow
  ↓
  reservation-container

PASO 2: Crear una clase CSS para este contenedor en .component.css
  .reservation-container {
    padding: var(--spacing-lg) var(--spacing-md);
    background-color: var(--surface-high);
    border-radius: var(--radius-md);
    box-shadow: var(--shadow-sm);
  }

PASO 3: Para botones/tarjetas/insignias reutilizables, utilizar las clases globales
  ✅ <button class="btn btn-primary">
  ❌ <button class="px-4 py-2 bg-blue-600 ...">

PASO 4: Utilizar variables CSS en lugar de valores hardcodeados
  ✅ background: var(--primary);
  ✅ padding: var(--spacing-md);
  ❌ background: #0066cc;
  ❌ padding: 16px;
*/

/*
---
LISTA DE VERIFICACIÓN DE MIGRACIÓN PARA CADA COMPONENTE
*/

/*
[ ] Reemplazar las clases Tailwind por nombres específicos del componente
[ ] Crear .component.css si no existe
[ ] Copiar los estilos en .component.css
[ ] Reemplazar todos los valores hardcodeados por variables CSS
[ ] Utilizar las clases globales (.btn, .card, .badge, .form-input, etc.)
[ ] Probar la encapsulación CSS (estilos se aplican SÓLO al componente)
[ ] Verificar la responsividad con @media queries
[ ] Validar la accesibilidad (focus-visible, contraste)
*/

/*
---
EJEMPLOS COMPLETOS
*/

/* ========== EJEMPLO 1: Tarjeta con Formulario ========== */

/*
HTML:
<div class="reservation-card">
  <h3>Nueva Reserva</h3>
  <div class="form-group">
    <label class="form-label">Fecha</label>
    <input type="date" class="form-input" />
  </div>
  <button class="btn btn-primary">Reservar</button>
</div>

CSS: reservation-list.component.css
*/

.reservation-card {
  padding: var(--spacing-lg);
  background: var(--surface-high);
  border: var(--border-width-1) solid var(--border-color);
  border-radius: var(--radius-md);
  margin-bottom: var(--spacing-md);
}

.reservation-card h3 {
  font: var(--font-headline-sm);
  margin-bottom: var(--spacing-md);
  color: var(--on-surface);
}

/* El .form-group y .btn provienen de components.css */

/* ========== EJEMPLO 2: Cuadrícula de Reservas ========== */

/*
HTML:
<div class="reservations-grid">
  <div class="reservation-item" *ngFor="let res of reservations">
    <h4>{{ res.title }}</h4>
    <p>{{ res.date }}</p>
    <div class="item-actions">
      <button class="btn btn-sm btn-ghost">Editar</button>
      <button class="btn btn-sm btn-danger">Eliminar</button>
    </div>
  </div>
</div>

CSS: reservation-list.component.css
*/

.reservations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.reservation-item {
  padding: var(--spacing-md);
  background: var(--surface-high);
  border: var(--border-width-1) solid var(--border-color);
  border-radius: var(--radius-md);
  transition: box-shadow var(--transition-fast);
}

.reservation-item:hover {
  box-shadow: var(--shadow-md);
}

.reservation-item h4 {
  font: var(--font-title-md);
  margin-bottom: var(--spacing-xs);
}

.reservation-item p {
  color: var(--on-surface-variant);
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-md);
}

.item-actions {
  display: flex;
  gap: var(--spacing-sm);
}

.item-actions button {
  flex: 1;
}

/* ========== EJEMPLO 3: Animación Específica del Componente ========== */

/*
HTML:
<div class="reservation-loading">
  <div class="loader"></div>
  <p>Cargando reservas...</p>
</div>

CSS: reservation-list.component.css
*/

.reservation-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-2xl);
  color: var(--on-surface-variant);
}

.loader {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-color);
  border-top-color: var(--primary);
  border-radius: 50%;
  animation: spin-loader 1s linear infinite;
}

@keyframes spin-loader {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* ========== RESPONSIVO ========== */

@media (max-width: 640px) {
  .reservations-grid {
    grid-template-columns: 1fr;
  }

  .reservation-container {
    padding: var(--spacing-md);
  }
}

/*
---
RECURSOS
*/

/*
📖 Documentación completa: src/app/styles/README.md
🎨 Variables disponibles: src/app/styles/variables.css
🧩 Componentes reutilizables: src/app/styles/components.css
*/
