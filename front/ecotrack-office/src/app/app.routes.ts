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

export const routes: Routes = [
  // RUTAS PÚBLICAS
  { path: '', component: HomeLanding },
  { path: 'login', component: LoginComponent },

  // RUTAS PRIVADAS
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
        canActivate: [authGuard], // Tip: Te recomiendo ponérselo también a home para que sea privada
      },
      {
        path: 'admin/user-management',
        component: UserManagementComponent,
        canActivate: [authGuard, adminGuard], // 2. ¡Aquí ocurre el doble candado! Debe estar logueado Y ser Admin
      },
      {
        path: 'reservation',
        component: Reservation,
        canActivate: [authGuard],
      },
      {
        path: 'organization',
        component: Organization,
        canActivate: [authGuard],
      },
      {
        // TODO: Filtrar solo los admins
        path: 'assets',
        component: AssetsMgmt,
        canActivate: [authGuard],
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
