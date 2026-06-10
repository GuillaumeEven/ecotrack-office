import { Routes } from '@angular/router';
import { UserProfileComponent } from './features/user-profile/user-profile.component';

export const routes: Routes = [
  { path: 'profile', component: UserProfileComponent },
  { path: '', redirectTo: 'profile', pathMatch: 'full' },
];
