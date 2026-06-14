import { Routes } from '@angular/router';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { LoginComponent } from './features/auth/login/login.component';
import { authGuard } from './guards/auth.guard';
import { HomeLanding } from './landing/components/home-landing/home-landing';
import { PrivateLayout } from './layouts/private-layout/private-layout';
import { AssetsMgmtComponent } from './assets-mgmt/components/assets-mgmt.component';

export const routes: Routes = [

  // RUTA PÚBLICA 

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
        path: 'assets-mgmt',
        component: AssetsMgmtComponent,
      }
    ],
  },

  // RUTA POR DEFECTO

  { path: '**', redirectTo: '' }
];
