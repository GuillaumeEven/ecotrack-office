import { Routes } from '@angular/router';
import { AssetsMgmtComponent } from './assets-mgmt/components/assets-mgmt.component';

export const routes: Routes = [
  {
    path: 'assets-mgmt',
    component: AssetsMgmtComponent
  },
  {
    path: '',
    redirectTo: 'assets-mgmt',
    pathMatch: 'full'
  }
];
