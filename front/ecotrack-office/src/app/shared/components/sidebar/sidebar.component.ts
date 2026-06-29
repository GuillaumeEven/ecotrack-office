import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

export interface NavItem {
  icon: string;
  label: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {

  // get the logged-in user's role
  role: string = '';

  constructor(private authService: AuthService) {
    this.role = this.authService.getRole() || '';
    this.filterNavItems();
  }

  navItems: NavItem[] = [
    { icon: 'dashboard', label: 'Hacer reserva', route: '/home' },
    { icon: 'calendar_month', label: 'Reservas', route: '/reservation' },
    { icon: 'confirmation_number', label: 'Incidencias', route: '/incidents' },
    { icon: 'group', label: 'Empleados', route: '/admin/user-management' },
    { icon: 'domain', label: 'Empresa', route: '/organization' },
    { icon: 'map', label: 'Gestionar espacio', route: '/assets'},
    { icon: 'insights', label: 'Analíticas', route: '/analytics' },
  ];

  bottomItems: NavItem[] = [
    { icon: 'settings', label: 'Settings', route: '/settings' },
    { icon: 'help', label: 'Support', route: '/support' },
  ];

  private filterNavItems(): void {
    if (this.role !== 'ADMIN') {
      this.navItems = this.navItems.filter(
        item => item.label !== 'Empleados' && item.label !== 'Empresa' && item.label !== 'Gestionar espacio'
      );
    }
  }
}
