import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterLinkActive } from '@angular/router';

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
})
export class SidebarComponent {
  navItems: NavItem[] = [
    { icon: 'dashboard', label: 'Dashboard', route: '/dashboard' },
    { icon: 'calendar_month', label: 'Room Booking', route: '/bookings' },
    { icon: 'confirmation_number', label: 'Tickets', route: '/tickets' },
    { icon: 'group', label: 'Employees', route: '/employees' },
    { icon: 'insights', label: 'Reports', route: '/reports' },
  ];

  bottomItems: NavItem[] = [
    { icon: 'settings', label: 'Settings', route: '/settings' },
    { icon: 'help', label: 'Support', route: '/support' },
  ];
}
