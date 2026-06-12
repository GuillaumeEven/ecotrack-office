import { Routes } from '@angular/router';
import { HomeLanding } from './landing/components/home-landing/home-landing';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { AssetsMgmtComponent } from './assets-mgmt/components/assets-mgmt.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'home', component: AssetsMgmtComponent },
  { path: 'profile', component: UserProfileComponent, canActivate: [authGuard] },
  { path: '', component: HomeLanding },
  { path: 'assets-mgmt', component: AssetsMgmtComponent },
];
