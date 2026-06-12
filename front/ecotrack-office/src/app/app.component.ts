import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './shared/components/header/header.component';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, HeaderComponent, SidebarComponent],
  template: `
    <ng-container *ngIf="authService.isAuthenticated()">
      <app-header />
      <app-sidebar />
    </ng-container>
    <router-outlet />
  `,
})
export class AppComponent {
  constructor(public authService: AuthService) {}
}
