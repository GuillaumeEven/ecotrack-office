import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


import { ReservationResponseModel, ReservationResponseWithNameModel } from '@models/index.model';
import { ReservationService, NotificationService } from '@services/index.service';

@Component({
  selector: 'app-reservation',
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation.component.html',
  styleUrls: ['./reservation.component.css'],
})
export class ReservationComponent implements OnInit{

  private reservationService = inject(ReservationService);
  private notificationService = inject(NotificationService);
  private cdr = inject(ChangeDetectorRef);

  activeReservations: ReservationResponseModel[] = [];
  unactiveReservations: ReservationResponseModel[] = [];

  allStaffReservations: ReservationResponseWithNameModel[] = [];

  isEditModalOpen = false;
  isCancelModalOpen = false;
  selectedReservation: ReservationResponseModel | null = null;
  currentTab: 'MY_BOOKINGS' | 'ALL_STAFF' = 'MY_BOOKINGS';

  newReservationDate: string = '';
  minDate: string = '';
  role: string = '';

  ngOnInit(): void {

    this.role = localStorage.getItem('user_role') || '';
    this.loadReservations();
    this.calculateMinDate();
  }

  private loadReservations(): void {

    this.reservationService.getReservationsByUser().subscribe({

      next: (data) => {

        const today = new Date();
        today.setHours(0, 0, 0, 0);
        this.activeReservations = data.filter(reserva => {

          const fechaReserva = new Date(reserva.date);
          fechaReserva.setHours(0, 0, 0, 0);
          return fechaReserva.getTime() >= today.getTime();
        });
        this.unactiveReservations = data.filter(reserva => {

          const fechaReserva = new Date(reserva.date);
          fechaReserva.setHours(0, 0, 0, 0);
          return fechaReserva.getTime() < today.getTime();
        });
        this.cdr.detectChanges();
      },
      error: (err) => {

        console.error('Error al traer las reservas: ', err);
      }
    });
  }

  private loadAllStaffReservations(): void {

    this.reservationService.getAllReservations().subscribe({

      next: (data) => {

        const today = new Date();
        today.setHours(0, 0, 0, 0);
        this.allStaffReservations = data.filter(reserva => {

          const fechaReserva = new Date(reserva.date);
          fechaReserva.setHours(0, 0, 0, 0);
          return fechaReserva.getTime() >= today.getTime();
        });
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error al traer todas las reservas de la empresa: ', err)
    });
  }

  switchTab(tab: 'MY_BOOKINGS' | 'ALL_STAFF'): void {

    this.currentTab = tab;
    if(tab === 'ALL_STAFF') {

      this.loadAllStaffReservations();
    } else {

      this.loadReservations();
    }
  }

  private calculateMinDate(): void {

    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth()+1).padStart(2, '0');
    const day = String(today.getDay()).padStart(2, '0');
    this.minDate = `${year}-${month}-¢{day}`;
  }

  openEditModal(reserva: ReservationResponseModel): void {

    this.selectedReservation = reserva;
    this.newReservationDate = reserva.date;
    this.isEditModalOpen = true;
  }

  closeEditModal(): void {

    this.isEditModalOpen = false;
    this.selectedReservation = null;
  }

  confirmEdit(): void {

    if(!this.selectedReservation || !this.newReservationDate) return;

    const idRecurso = this.selectedReservation.id;
    const newDate = this.newReservationDate;
    const newStatus = this.selectedReservation.status;
    const newUserId = this.selectedReservation.userId;
    this.closeEditModal();
    this.reservationService.updateReservation(idRecurso,

      {
        date: newDate,
        status: newStatus,
        userId: newUserId

      }).subscribe({

      next: () => {

        this.refreshCurrentTab();
        this.notificationService.success('¡Reserva editada con éxito!')
      },
      error: (err) => {

        console.error('Error al actualizar la reserva: ', err);
      }
    });
  }

  openCancelModal(reserva: ReservationResponseModel): void {

    this.selectedReservation = reserva;
    this.isCancelModalOpen = true;
  }

  closeCancelModal(): void {

    this.isCancelModalOpen = false;
    this.selectedReservation = null;
  }

  confirmCancel(): void {

    if(!this.selectedReservation) return;
    const idRecurso = this.selectedReservation.id;
    this.closeCancelModal();
    this.reservationService.deleteReservation(idRecurso).subscribe({

      next: () => {

        this.refreshCurrentTab();
        this.notificationService.success('!Éxito al cancelar la reserva¡');
      },
      error: (err) => {

        console.error('Error al cancelar: ', err);
      }
    });
  }

  private refreshCurrentTab(): void {

    if(this.currentTab === 'ALL_STAFF'){

      this.loadAllStaffReservations();
    } else{

      this.loadReservations();
    }
  }
}
