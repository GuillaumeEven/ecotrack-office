import { Routes } from '@angular/router';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { LoginComponent } from './features/auth/login/login.component';
import { authGuard } from './guards/auth.guard';
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

  // RUTAS PRIVADAS (Cargan dentro del menú de la aplicación)
  {
    path: '',
    component: PrivateLayout,
    children: [
      {
        path: 'profile',
        component: UserProfileComponent,
        canActivate: [authGuard],
      },
      {
        path: 'home',
        component: BuildingMapComponent,
      },
      {
        path: 'admin/user-management',
        component: UserManagementComponent,
        canActivate: [authGuard],
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