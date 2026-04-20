# Story 0.1: Initialize Angular Frontend Workspace

Status: ready-for-dev

## Story

As a developer,
I want a fully scaffolded Angular 21 workspace with routing, SCSS, strict TypeScript, Angular Material, and the core/shared module structure,
so that all four feature teams can start implementing their feature modules against a consistent foundation from day one.

## Acceptance Criteria

1. `ng new ecotrack-office-frontend --routing --style scss --strict` executes successfully inside `frontend/`
2. Angular Material is added via `ng add @angular/material` with a custom Teal (#00897B) theme
3. `core/` module exists with:
   - `auth/` folder containing stub files: `auth.service.ts`, `auth.guard.ts`, `role.guard.ts`, `jwt.service.ts`
   - `interceptors/` folder containing stub files: `auth.interceptor.ts`, `error.interceptor.ts`
   - `sse/` folder containing stub: `sse-notification.service.ts`
4. `shared/` module exists with:
   - `components/loading-spinner/`, `components/confirm-dialog/`, `components/error-banner/` (stub components)
   - `pipes/relative-time.pipe.ts` (stub pipe)
   - `material.module.ts` re-exporting Angular Material modules used across all feature modules
5. `app.routes.ts` contains lazy-loaded stub routes for: `users/`, `assets-mgmt/`, `reservations/`, `analytics/`
6. `environments/environment.ts` and `environments/environment.prod.ts` contain `apiUrl`, `sseUrl`, `pollInterval`
7. `ng lint` passes with zero errors
8. `ng build` passes with zero errors

## Tasks / Subtasks

- [ ] Task 1 — Bootstrap Angular workspace (AC: #1)
  - [ ] 1.1 Run `ng new ecotrack-office-frontend --routing --style scss --strict` inside `frontend/`
  - [ ] 1.2 Verify `tsconfig.json` has `"strict": true`, `"strictTemplates": true`

- [ ] Task 2 — Add Angular Material + custom theme (AC: #2)
  - [ ] 2.1 Run `ng add @angular/material` — choose "Custom" when prompted for a theme
  - [ ] 2.2 In `src/styles.scss` define the M3 custom theme with Teal as primary (`#00897B`)
  - [ ] 2.3 Export CSS custom properties: `--ecotrack-primary: #00897B`, `--ecotrack-primary-light: #4DB6AC`, `--ecotrack-primary-dark: #005B4F`
  - [ ] 2.4 Set Roboto as the app font (Angular Material default — no extra import needed)

- [ ] Task 3 — Create `core/` structure (AC: #3)
  - [ ] 3.1 Create `src/app/core/auth/auth.service.ts` — inject `HttpClient`, expose `currentUser$: Observable<UserProfile | null>`, stub `login()`, `logout()`, `register()` methods
  - [ ] 3.2 Create `src/app/core/auth/auth.guard.ts` — `CanActivateFn` stub, returns `true` (to be implemented in Story 1.2)
  - [ ] 3.3 Create `src/app/core/auth/role.guard.ts` — `CanActivateFn` stub returning `true`
  - [ ] 3.4 Create `src/app/core/auth/jwt.service.ts` — stub class with `parseToken()` method
  - [ ] 3.5 Create `src/app/core/interceptors/auth.interceptor.ts` — `HttpInterceptorFn` stub (passes request through unchanged)
  - [ ] 3.6 Create `src/app/core/interceptors/error.interceptor.ts` — `HttpInterceptorFn` stub (passes error through via `throwError`)
  - [ ] 3.7 Create `src/app/core/sse/sse-notification.service.ts` — inject `isPlatformBrowser`, stub `connect()` and `disconnect()` methods

- [ ] Task 4 — Create `shared/` structure (AC: #4)
  - [ ] 4.1 Create `src/app/shared/material.module.ts` — `@NgModule` that imports and exports: `MatButtonModule`, `MatInputModule`, `MatFormFieldModule`, `MatIconModule`, `MatToolbarModule`, `MatSidenavModule`, `MatListModule`, `MatCardModule`, `MatDialogModule`, `MatSnackBarModule`, `MatProgressSpinnerModule`, `MatChipsModule`, `MatMenuModule`, `MatTableModule`, `MatTooltipModule`
  - [ ] 4.2 Create stub `LoadingSpinnerComponent` (standalone) in `shared/components/loading-spinner/`
  - [ ] 4.3 Create stub `ConfirmDialogComponent` (standalone) in `shared/components/confirm-dialog/`
  - [ ] 4.4 Create stub `ErrorBannerComponent` (standalone) in `shared/components/error-banner/`
  - [ ] 4.5 Create stub `RelativeTimePipe` in `shared/pipes/relative-time.pipe.ts`

- [ ] Task 5 — Configure routing with 4 lazy-loaded feature stubs (AC: #5)
  - [ ] 5.1 Update `src/app/app.routes.ts` with lazy routes (see Dev Notes below)
  - [ ] 5.2 Create placeholder route files: `users/users.routes.ts`, `assets-mgmt/assets.routes.ts`, `reservations/reservations.routes.ts`, `analytics/analytics.routes.ts` — each exports an empty `Routes` array
  - [ ] 5.3 Create one stub component per module (e.g., `UsersHomeComponent`) so the lazy load resolves without error

- [ ] Task 6 — Environment files (AC: #6)
  - [ ] 6.1 Create `src/environments/environment.ts` with `apiUrl`, `sseUrl`, `pollInterval`
  - [ ] 6.2 Create `src/environments/environment.prod.ts` with production values (empty `apiUrl` is fine — will be set via CI/CD)
  - [ ] 6.3 Register file replacements in `angular.json` under `configurations.production`

- [ ] Task 7 — Update `app.config.ts` (AC: #1, #3)
  - [ ] 7.1 Add `provideHttpClient(withInterceptors([authInterceptor, errorInterceptor]))` 
  - [ ] 7.2 Add `provideRouter(routes, withComponentInputBinding())`
  - [ ] 7.3 Do NOT add `provideAnimations()` — use `provideAnimationsAsync()` instead (Angular 17+ default)

- [ ] Task 8 — Verify build and lint (AC: #7, #8)
  - [ ] 8.1 Run `ng lint` — fix all reported issues before proceeding
  - [ ] 8.2 Run `ng build` — must complete with zero errors and zero warnings related to structure
  - [ ] 8.3 Run `ng test --watch=false` — at least the default `AppComponent` spec should pass

## Dev Notes

### Angular 21 Specifics — CRITICAL

Angular 21 uses **standalone components by default** — there is no `AppModule`. The project uses:
- `app.config.ts` instead of `AppModule` for providers
- `app.routes.ts` for routing
- All new components must use `standalone: true` (this is the default in Angular 17+)
- Angular signals (`signal()`, `computed()`, `effect()`) for reactive state in stateful components — NOT `BehaviorSubject` in components
- `BehaviorSubject` is still used in **services** to expose observables to consumers

### Required `app.routes.ts` content

```typescript
import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'users',
    loadChildren: () => import('./users/users.routes').then(m => m.USERS_ROUTES)
  },
  {
    path: 'assets-mgmt',
    loadChildren: () => import('./assets-mgmt/assets.routes').then(m => m.ASSETS_ROUTES)
  },
  {
    path: 'reservations',
    loadChildren: () => import('./reservations/reservations.routes').then(m => m.RESERVATIONS_ROUTES)
  },
  {
    path: 'analytics',
    loadChildren: () => import('./analytics/analytics.routes').then(m => m.ANALYTICS_ROUTES)
  },
  { path: '', redirectTo: 'assets-mgmt', pathMatch: 'full' },
  { path: '**', redirectTo: 'assets-mgmt' }
];
```

> The final default redirect (`''`) will be updated in Story 1.2 (auth guard) to route employees to `/floor-map` and admins to `/dashboard`. Keep the stub redirect to `assets-mgmt` for now.

### Required environment files

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api/v1',
  sseUrl: 'http://localhost:8080/api/v1/sse',
  pollInterval: 30000  // ms — availability polling interval
};

// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: '',      // Set via ANGULAR_API_URL env var at build time
  sseUrl: '',
  pollInterval: 30000
};
```

### Angular Material custom theme (`styles.scss`)

```scss
@use '@angular/material' as mat;

// Define EcoTrack palette from Teal
$ecotrack-primary: mat.m3-define-theme((
  color: (
    theme-type: light,
    primary: mat.$teal-palette,
  ),
  typography: (
    plain-family: 'Roboto, sans-serif',
  ),
));

html {
  @include mat.all-component-themes($ecotrack-primary);
}

// CSS custom properties for cross-component use
:root {
  --ecotrack-primary: #00897B;       // Teal 600 — 4.6:1 contrast on white ✅ WCAG AA
  --ecotrack-primary-light: #4DB6AC; // Teal 300 — hover/focus states
  --ecotrack-primary-dark: #005B4F;  // Teal 800 — active/pressed states
  --ecotrack-spacing-base: 8px;      // 8px grid unit
}
```

### Service pattern — BehaviorSubject (in services only)

```typescript
// ✅ CORRECT — always private Subject, public Observable
private currentUserSubject = new BehaviorSubject<UserProfile | null>(null);
currentUser$ = this.currentUserSubject.asObservable();

// ❌ NEVER expose the Subject directly
public currentUserSubject = new BehaviorSubject<UserProfile | null>(null);
```

### Interceptor pattern — functional style (Angular 15+)

```typescript
// auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Stub — JWT cookie is sent automatically (HttpOnly cookie)
  // No manual header injection needed at this stage
  return next(req);
};

// error.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError(err => throwError(() => err))  // Stub — RFC 7807 parsing added in Story 1.2
  );
};
```

### Stub component pattern (standalone)

```typescript
// loading-spinner.component.ts
import { Component } from '@angular/core';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [MatProgressSpinnerModule],
  template: `<mat-spinner diameter="40" />`
})
export class LoadingSpinnerComponent {}
```

### Project Structure Notes

This story creates **only** the `frontend/` subtree. The exact paths to create:

```
frontend/
├── angular.json
├── package.json
├── tsconfig.json
├── tsconfig.app.json
├── tsconfig.spec.json
│
└── src/
    ├── main.ts
    ├── index.html
    ├── styles.scss                         ← Angular Material theme + CSS custom properties
    │
    ├── environments/
    │   ├── environment.ts
    │   └── environment.prod.ts
    │
    └── app/
        ├── app.component.ts                ← Standalone, no NgModule
        ├── app.routes.ts                   ← 4 lazy routes + default redirect
        ├── app.config.ts                   ← provideHttpClient + provideRouter + provideAnimationsAsync
        │
        ├── core/
        │   ├── auth/
        │   │   ├── auth.service.ts
        │   │   ├── auth.guard.ts
        │   │   ├── role.guard.ts
        │   │   └── jwt.service.ts
        │   ├── interceptors/
        │   │   ├── auth.interceptor.ts
        │   │   └── error.interceptor.ts
        │   └── sse/
        │       └── sse-notification.service.ts
        │
        ├── shared/
        │   ├── material.module.ts
        │   ├── components/
        │   │   ├── loading-spinner/
        │   │   │   └── loading-spinner.component.ts
        │   │   ├── confirm-dialog/
        │   │   │   └── confirm-dialog.component.ts
        │   │   └── error-banner/
        │   │       └── error-banner.component.ts
        │   └── pipes/
        │       └── relative-time.pipe.ts
        │
        ├── users/
        │   ├── users.routes.ts
        │   └── users-home.component.ts     ← minimal stub for lazy route resolution
        ├── assets-mgmt/
        │   ├── assets.routes.ts
        │   └── assets-home.component.ts
        ├── reservations/
        │   ├── reservations.routes.ts
        │   └── reservations-home.component.ts
        └── analytics/
            ├── analytics.routes.ts
            └── analytics-home.component.ts
```

> **Do NOT** create `backend/` files in this story — that is Story 0.2.
> **Do NOT** create `docker-compose.yml` — that is Story 0.3.

### Architecture Constraints (MUST follow)

- **No NgRx** — state management via Services + RxJS BehaviorSubject only
- **Reactive Forms** for all forms (never Template-driven)
- **TypeScript strict mode** must remain enabled — no `// @ts-ignore`
- **File naming**: kebab-case (e.g., `auth.service.ts`, `floor-map.component.ts`)
- **Class naming**: PascalCase (e.g., `AuthService`, `FloorMapComponent`)
- **Interface naming**: no I-prefix (e.g., `UserProfile`, not `IUserProfile`)
- **Enum naming**: `ReservationStatus.CONFIRMED`, not string literals
- **Angular Material version**: whatever version ships with Angular 21 (use `ng add @angular/material`)
- `provideAnimationsAsync()` — NOT `provideAnimations()` (deprecated in Angular 17+)

### Potential Pitfalls

1. **`ng add @angular/material` prompt** — When asked "Choose a prebuilt theme name, or 'custom' for a custom theme" → select **Custom**. Otherwise the default theme will override your Teal setup.
2. **`withInterceptors()` API** — Functional interceptors must be registered via `withInterceptors([...])` in `provideHttpClient()`. The old class-based `HTTP_INTERCEPTORS` token is deprecated.
3. **`app.component.ts` default template** — The generated template includes an Angular logo; replace with a simple `<router-outlet />` wrapper.
4. **ESLint** — Angular CLI 21 ships with ESLint by default. Run `ng lint --fix` before `ng lint` to auto-fix formatting issues.

### References

- Angular 21 architecture: [Source: architecture.md#Frontend-Starter-Angular-CLI-21]
- Module structure: [Source: architecture.md#Structure-Patterns]
- Interceptor pattern: [Source: architecture.md#Frontend-Architecture]
- Anti-pattern (BehaviorSubject): [Source: architecture.md#Enforcement-Guidelines]
- Theme colors: [Source: ux-design-specification.md#Visual-Foundation]
- Contrast ratios: [Source: ux-design-specification.md — Teal #00897B on white = 4.6:1 ✅ WCAG AA]
- Lazy routes + module scope: [Source: architecture.md#Complete-Project-Directory-Structure]
- Story acceptance criteria: [Source: epics.md#Story-0.1]

## Dev Agent Record

### Agent Model Used

Claude Sonnet 4.6 (bmad-create-story workflow)

### Debug Log References

_None_

### Completion Notes List

_To be filled by the developer after implementation._

### File List

_To be filled by the developer: list every file created or modified._
