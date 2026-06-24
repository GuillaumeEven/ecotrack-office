import { Routes } from '@angular/router';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { LoginComponent } from './features/auth/login/login.component';
import { authGuard } from './guards/auth.guard';
import { adminGuard } from './guards/admin.guard';
import { HomeLanding } from './landing/components/home-landing/home-landing';
import { PrivateLayout } from './layouts/private-layout/private-layout';
import { BuildingMapComponent } from './building-map/components/building-map.component';
import { UserManagementComponent } from './features/admin/user-management/user-management.component';
import { Reservation } from './reservation/reservation';
import { Organization } from './organization/organization';
import { AssetsMgmt } from './features/assets-mgmt/assets-mgmt';

// Nuevos componentes añadidos para la tarea EK-27
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
    canActivate: [authGuard], // 🔒 PADRE BLINDADO: Aplica 'authGuard' a todos los hijos automáticamente
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
        canActivate: [adminGuard], // 🛡️ Filtro extra: Además de estar logueado (por el padre), debe ser ADMIN
      },
      {
        path: 'reservation',
        component: Reservation,
      },
      {
        path: 'organization',
        component: Organization,
      },
      {
        // TODO: Filtrar solo los admins
        path: 'assets',
        component: AssetsMgmt,
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
  // RUTA POR DEFECTO (Redirección si la URL no existe)
  { path: '**', redirectTo: '' },
];