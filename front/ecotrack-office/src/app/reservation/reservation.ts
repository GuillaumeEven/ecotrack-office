import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { ReservationResponse } from '@models/reservation.model';
import { ReservationService } from '../services/reservation.service';

@Component({
  selector: 'app-reservation',
  imports: [],
  templateUrl: './reservation.html',
  styleUrl: './reservation.css',
})
export class Reservation implements OnInit{

  private reservationService = inject(ReservationService);
  private cdr = inject(ChangeDetectorRef);

  activeReservations: ReservationResponse[] = [];

  //ID DE PRUEBA LUEGO TENGO QUE UTILIZAR EL TOKEN
  private userIdActivo = 1;

  ngOnInit(): void {
    
    this.loadReservations();
  }

  private loadReservations(): void {

    this.reservationService.getReservationByUser(this.userIdActivo).subscribe({

      next: (data) => {

        this.activeReservations = data;
        this.cdr.detectChanges();
      },
      error: (err) => {

        console.error('Error al traer las reservas: ', err);
      }
    });
  }

  editReservation(id: number): void {

    console.log('ABRIR EDITOR PARA LA RESERVA CON ID: ',id);
  }

  cancelReservation(id: number): void {

    const confirmar = confirm('¿Estás seguro de que deseas cancelar esta reserva?');
    if(confirmar) {

      this.reservationService.deleteReservation(id).subscribe({

        next: () => {

          alert('Reserva candelada con éxito');
          this.loadReservations();
        },
        error: (err) => {

          console.error('Error al cancelar:', err);
          alert('No se pudo cancelar la reserva');
        }
      });
    }
  }
}
