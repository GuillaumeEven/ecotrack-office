import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
})
export class HeaderComponent {
  onLogout(): void {
    console.log('Logout clicked');
    // TODO: conectar con AuthService del backend
  }
}
