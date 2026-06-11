import { Routes } from '@angular/router';
import { HomeLanding } from './landing/components/home-landing/home-landing';
import { UserProfileComponent } from './features/user-profile/user-profile.component';

export const routes: Routes = [
  { path: 'profile', component: UserProfileComponent },
  { path: '', component: HomeLanding}

];
