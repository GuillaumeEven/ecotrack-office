import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { ThemeService, Theme } from '../../../services/theme.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {

  private authService = inject(AuthService);
  public themeService = inject(ThemeService);

  /**
   * Observable del tema actual
   */
  currentTheme$ = this.themeService.getTheme$();

  /**
   * Alterna el tema entre light y dark
   */
  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  onLogout(): void {
    this.authService.logout();
  }
}

