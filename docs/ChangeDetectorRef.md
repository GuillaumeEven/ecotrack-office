# ChangeDetectorRef - markForCheck()

## ¿Qué es?
`ChangeDetectorRef` es un servicio de Angular que permite controlar manualmente la detección de cambios en un componente.

## ¿Cuándo usar markForCheck()?
Cuando un cambio de estado NO se refleja automáticamente en la UI, especialmente después de operaciones asincrónicas (HTTP requests, observables).

## Ejemplo
```typescript
import { ChangeDetectorRef } from '@angular/core';

constructor(private cdr: ChangeDetectorRef) {}

confirmDelete() {
  this.service.delete(id).subscribe({
    next: () => {
      this.isOpen = false;  // Cambio de estado
      this.cdr.markForCheck();  // Fuerza detección de cambio
    }
  });
}
```

## Síntomas de necesidad
- Dialog/modal no se cierra después de operación exitosa
- Estado cambia en TypeScript pero no en el template
- Console muestra todo correcto pero UI no actualiza

## Alternativas
- `detectChanges()`: Más invasivo, ejecuta detección inmediatamente
- `markForCheck()`: Marca para verificación en próximo ciclo (recomendado)
