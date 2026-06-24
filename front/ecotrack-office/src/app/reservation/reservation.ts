import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ReservationResponse } from '@models/reservation.model';
import { ReservationService } from '../services/reservation.service';

@Component({
  selector: 'app-reservation',
  imports: [CommonModule, FormsModule],
  templateUrl: './reservation.html',
  styleUrl: './reservation.css',
})
export class Reservation implements OnInit{

  private reservationService = inject(ReservationService);
  private cdr = inject(ChangeDetectorRef);

  activeReservations: ReservationResponse[] = [];
  unactiveReservations: ReservationResponse[] = [];

  allStaffReservations: ReservationResponse[] = [];

  isEditModalOpen = false;
  isCancelModalOpen = false;
  selectedReservation: ReservationResponse | null = null;
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

        this.allStaffReservations = data;
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

  openEditModal(reserva: ReservationResponse): void {

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
    this.reservationService.updateReservation(this.selectedReservation.id, 
      
      {
        date: this.newReservationDate,
        status: this.selectedReservation.status,
        userId: this.selectedReservation.userId

      }).subscribe({

      next: () => {

        alert('Reserva actualizada con éxito');
        this.closeEditModal();
        this.loadReservations();
      },
      error: (err) => {

        console.error('Error al actualizar la reserva: ', err);
        alert('No se pudo actualizar la reserva');
      }
    });
  }

  openCancelModal(reserva: ReservationResponse): void {

    this.selectedReservation = reserva;
    this.isCancelModalOpen = true;
  }

  closeCancelModal(): void {

    this.isCancelModalOpen = false;
    this.selectedReservation = null;
  }

  confirmCancel(): void {

    if(!this.selectedReservation) return;
    this.reservationService.deleteReservation(this.selectedReservation.id).subscribe({

      next: () => {

        alert('Reserva cancelada con éxito');
        this.closeCancelModal();
        this.loadReservations();
      },
      error: (err) => {

        console.error('Error al cancelar: ', err);
        alert('No se pudo cancelar la reserva');
      }
    });
  }
}
