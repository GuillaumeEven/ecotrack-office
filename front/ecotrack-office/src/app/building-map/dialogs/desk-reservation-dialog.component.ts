import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DeskWithStatus, ResourceStatus } from '../models';
import { ReservationService } from '../services';
import { AuthService } from '../../services/auth.service';
import { NotificationService } from '@core/services/notification.service';
import { throwError } from 'rxjs';

@Component({
  selector: 'app-desk-reservation-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './desk-reservation-dialog.component.html',
  styleUrls: ['./desk-reservation-dialog.component.css']
})
export class DeskReservationDialogComponent implements OnInit {
  @Input() isOpen = false;
  @Input() deskWithStatus: DeskWithStatus | null = null;
  @Input() selectedDate: Date = new Date();
  @Input() isMeetingRoom = false; // Flag to indicate if this is a meeting room reservation
  @Output() close = new EventEmitter<void>();
  @Output() reserved = new EventEmitter<void>();

  // UI State
  isLoading = false;

  ResourceStatus = ResourceStatus;
  private currentUserEmail: string | null = null;
  private successMessage: string | null = null;
  // private errorMessage: string | null = null;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.currentUserEmail = this.authService.getEmail();
  }

  isReserved(): boolean {
    return this.deskWithStatus?.calculatedStatus === ResourceStatus.RESERVED;
  }

  isUnavailable(): boolean {
    return this.deskWithStatus?.calculatedStatus === ResourceStatus.UNAVAILABLE;
  }

  isAvailable(): boolean {
    return this.deskWithStatus?.calculatedStatus === ResourceStatus.AVAILABLE;
  }

  getReservedByText(): string {
    if (!this.deskWithStatus?.reservedBy) return '';
    if (this.deskWithStatus.reservedBy === this.currentUserEmail) {
      return 'Your reservation';
    }
    return `Reserved by ${this.deskWithStatus.reservedBy}`;
  }

  isMyReservation(): boolean {
    return this.isReserved() &&
           this.deskWithStatus?.reservedBy === this.currentUserEmail;
  }

  isOtherReservation(): boolean {
    return this.isReserved() &&
           this.deskWithStatus?.reservedBy !== this.currentUserEmail;
  }

  /**
   * Check if user can cancel this reservation
   * Can cancel if: resource is reserved AND (it's my reservation OR user is ADMIN/TECHNICIAN)
   */
  canCancelReservation(): boolean {
    if (!this.isReserved()) {
      return false;
    }

    if (this.isMyReservation()) {
      return true;
    }

    // Check if user is ADMIN or TECHNICIAN
    const userRole = this.authService.getRole();
    return userRole === 'ADMIN' || userRole === 'TECHNICIAN';
  }

  /**
   * Get resource type text (Desk or Meeting Room)
   */
  getResourceType(): string {
    return this.isMeetingRoom ? 'Sala de reunión' : 'Escritorio';
  }

  onReserve(): void {
    if (!this.deskWithStatus?.desk?.id) {
      // this.errorMessage = 'ID de escritorio inválido';
      return;
    }

    const userId = this.authService.getUserId();
    if (!userId) {
      // this.errorMessage = 'Usuario no autenticado';
      return;
    }

    this.isLoading = true;
    this.successMessage = null;

    const dateStr = this.formatDateToISO(this.selectedDate);
    const userIdNum = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    this.reservationService
      .create(userIdNum, this.deskWithStatus.desk.id, dateStr)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          const resourceType = this.isMeetingRoom ? 'Meeting Room' : 'Desk';
          this.successMessage = `${resourceType} "${this.deskWithStatus?.desk?.name}" se ha reservado con éxtio!`;
          this.notificationService.success(this.successMessage);
          this.onClose();
          this.reserved.emit();
        },
        error: (error) => {
          this.isLoading = false;
        }
      });
  }

  onClose(): void {
    this.isOpen = false;
    this.successMessage = null;
    this.close.emit();
  }

  onCancelReservation(): void {
    if (!this.deskWithStatus?.reservationId) {
      // this.errorMessage = 'ID de reserva no encontrado';
      return;
    }

    if (!confirm('¿Está seguro de que desea cancelar esta reserva?')) {
      return;
    }

    this.isLoading = true;
    this.successMessage = null;

    this.reservationService
      .delete(this.deskWithStatus.reservationId)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          const resourceType = this.isMeetingRoom ? 'Meeting Room' : 'Desk';
          this.successMessage = `La reserva de ${resourceType} "${this.deskWithStatus?.desk?.name}" se ha cancelado correctamente!`;
          this.notificationService.success(this.successMessage);
          this.onClose();
          this.reserved.emit(); // Emit event to trigger refresh
        },
        error: (error) => {
          this.isLoading = false;
          // this.errorMessage =
          //   error.error?.message ||
          //   error.message ||
          //   `Error al cancelar la ${this.isMeetingRoom ? 'sala de reunión' : 'escritorio'} (HTTP ${error.status}). Por favor, inténtelo de nuevo.`;
        }
      });
  }

  /**
   * Format date to ISO string (YYYY-MM-DD) using local timezone
   * Prevents UTC conversion issues
   */
  private formatDateToISO(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}