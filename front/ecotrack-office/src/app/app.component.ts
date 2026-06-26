import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { ThemeService } from './services/theme.service';
import { ToastContainerComponent } from '@shared/components/toast-container/toast-container.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, ToastContainerComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  /**
   * ThemeService se inyecta para inicializar el tema al arrancar la app
   */
  constructor(
    public authService: AuthService,
    private themeService: ThemeService
  ) {}
}
