import { Routes } from '@angular/router';
import {
  UserProfileComponent,
  LoginComponent,
  LandingComponent,
  BuildingMapComponent,
  UserManagementComponent,
  IncidentsComponent,
  AnalyticsComponent,
  OrganizationComponent,
  AssetsMgmtComponent,
  ReservationComponent
 } from './features/index.component'; // Importamos UserProfileComponent desde index.component.ts
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { PrivateLayout } from './layouts/private-layout/private-layout';

export const routes: Routes = [
  // RUTAS PÚBLICAS
  { path: '', component: LandingComponent },
  { path: 'login', component: LoginComponent },

  // RUTAS PRIVADAS (Toda la sección requiere estar autenticado)
  {
    path: '',
    component: PrivateLayout,
    canActivate: [authGuard], // Filtro: Solo usuarios logueados pueden acceder a estas rutas
    children: [
      {
        path: 'profile',
        component: UserProfileComponent,
      },
      {
        path: 'home',
        component: BuildingMapComponent,
      },
      {
        path: 'admin/user-management',
        component: UserManagementComponent,
        canActivate: [adminGuard], // Filtro: Solo admins pueden acceder a esta ruta
      },
      {
        path: 'reservation',
        component: ReservationComponent,
      },
      {
        path: 'organization',
        component: OrganizationComponent,
        canActivate: [adminGuard], // 🛡️ Filtro extra: Además de estar logueado (por el padre), debe ser ADMIN
      },
      {
        path: 'assets',
        component: AssetsMgmtComponent,
        canActivate: [adminGuard], // 🛡️ Filtro extra: Además de estar logueado (por el padre), debe ser ADMIN
      },
      {
        path: 'incidents',
        component: IncidentsComponent,
      },
      {
        path: 'analytics',
        component: AnalyticsComponent,
      }
    ],
  },

  // COMODÍN: Redirección por defecto para URLs inexistentes
  { path: '**', redirectTo: 'login' },
];
