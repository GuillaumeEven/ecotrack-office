import { Routes } from '@angular/router';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { AssetsMgmtComponent } from './assets-mgmt/components/assets-mgmt.component';

export const routes: Routes = [
  { path: 'home', component: AssetsMgmtComponent },
  { path: 'profile', component: UserProfileComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },
];
