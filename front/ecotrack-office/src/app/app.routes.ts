import { Routes } from '@angular/router';
import { HomeLanding } from './landing/components/home-landing/home-landing';

export const routes: Routes = [

    { path: '', component: HomeLanding}
import { UserProfileComponent } from './features/user-profile/user-profile.component';

export const routes: Routes = [
  { path: 'profile', component: UserProfileComponent },
  { path: '', redirectTo: 'profile', pathMatch: 'full' },
];
