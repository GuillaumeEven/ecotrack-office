import { Routes } from '@angular/router';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { LoginComponent } from './features/auth/login/login.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { HomeLanding } from './landing/components/home-landing/home-landing';
import { PrivateLayout } from './layouts/private-layout/private-layout';
import { BuildingMapComponent } from './building-map/components/building-map.component';
import { UserManagementComponent } from './features/admin/user-management/user-management.component';
import { Reservation } from './reservation/reservation';
import { Organization } from './organization/organization';
import { AssetsMgmt } from './features/assets-mgmt/assets-mgmt';
import { IncidenciasComponent } from './features/incidencias/incidencias';
import { AnalyticsComponent } from './features/analytics/analytics';

export const routes: Routes = [
  // RUTAS PÚBLICAS
  { path: '', component: HomeLanding },
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
        component: Reservation,
      },
      {
        path: 'organization',
        component: Organization,
        canActivate: [adminGuard], // 🛡️ Filtro extra: Además de estar logueado (por el padre), debe ser ADMIN
      },
      {
        path: 'assets',
        component: AssetsMgmt,
        canActivate: [adminGuard], // 🛡️ Filtro extra: Además de estar logueado (por el padre), debe ser ADMIN
      },
      {
        path: 'incidencias',
        component: IncidenciasComponent,
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
