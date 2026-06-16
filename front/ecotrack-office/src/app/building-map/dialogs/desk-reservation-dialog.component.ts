import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DeskWithStatus, ResourceStatus } from '../models';
import { ReservationService } from '../services';
import { AuthService } from '../../services/auth.service';

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
  errorMessage: string | null = null;
  successMessage: string | null = null;

  ResourceStatus = ResourceStatus;
  private currentUserEmail: string | null = null;

  constructor(
    private reservationService: ReservationService,
    private authService: AuthService
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
    return this.isMeetingRoom ? 'Meeting Room' : 'Desk';
  }

  onReserve(): void {
    if (!this.deskWithStatus?.desk?.id) {
      this.errorMessage = 'Invalid desk ID';
      return;
    }

    const userId = this.authService.getUserId();
    if (!userId) {
      this.errorMessage = 'User not authenticated';
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;

    const dateStr = this.formatDateToISO(this.selectedDate);
    const userIdNum = typeof userId === 'string' ? parseInt(userId, 10) : userId;

    console.log('💾 Dialog - Attempting reservation:');
    console.log('   User ID:', userIdNum, 'Type:', typeof userIdNum);
    console.log('   Resource ID:', this.deskWithStatus.desk.id, 'Type:', typeof this.deskWithStatus.desk.id);
    console.log('   Date:', dateStr, 'Type:', typeof dateStr);
    console.log('   Current User Email:', this.currentUserEmail);

    this.reservationService
      .create(userIdNum, this.deskWithStatus.desk.id, dateStr)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log('✅ Reservation successful:', response);
          const resourceType = this.isMeetingRoom ? 'Meeting Room' : 'Desk';
          this.successMessage = `${resourceType} "${this.deskWithStatus?.desk?.name}" reserved successfully!`;
          setTimeout(() => {
            this.onClose();
            this.reserved.emit();
          }, 1500);
        },
        error: (error) => {
          this.isLoading = false;
          console.error('❌ Reservation failed:');
          console.error('   Status:', error.status);
          console.error('   Status Text:', error.statusText);
          console.error('   Error:', error.error);
          console.error('   Message:', error.message);
          console.error('   Full error:', error);

          this.errorMessage =
            error.error?.message ||
            error.message ||
            `Failed to reserve desk (HTTP ${error.status}). Please try again.`;
        }
      });
  }

  onClose(): void {
    this.isOpen = false;
    this.errorMessage = null;
    this.successMessage = null;
    this.close.emit();
  }

  onCancelReservation(): void {
    if (!this.deskWithStatus?.reservationId) {
      this.errorMessage = 'Reservation ID not found';
      return;
    }

    if (!confirm('Are you sure you want to cancel this reservation?')) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = null;
    this.successMessage = null;

    console.log('🗑️ Attempting to cancel reservation:', this.deskWithStatus.reservationId);

    this.reservationService
      .delete(this.deskWithStatus.reservationId)
      .subscribe({
        next: (response) => {
          this.isLoading = false;
          console.log('✅ Reservation cancelled successfully');
          const resourceType = this.isMeetingRoom ? 'Meeting Room' : 'Desk';
          this.successMessage = `${resourceType} "${this.deskWithStatus?.desk?.name}" cancelled successfully!`;
          setTimeout(() => {
            this.onClose();
            this.reserved.emit(); // Emit event to trigger refresh
          }, 1500);
        },
        error: (error) => {
          this.isLoading = false;
          console.error('❌ Cancellation failed:', error);
          this.errorMessage =
            error.error?.message ||
            error.message ||
            `Failed to cancel reservation (HTTP ${error.status}). Please try again.`;
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
