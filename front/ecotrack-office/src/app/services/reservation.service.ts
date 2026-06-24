import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReservationResponse } from '@models/reservation.model';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {

  private http = inject(HttpClient);
  private readonly API_URL = 'http://localhost:8080/api/v1/reservations';

  getReservationsByUser() : Observable<ReservationResponse[]> {

    return this.http.get<ReservationResponse[]>(`${this.API_URL}/user`);
  }

  //TODO: CAMBIAR POR LA LLAMADA REAL

  getAllReservations() : Observable<ReservationResponse[]> {

    return this.http.get<ReservationResponse[]>(`${this.API_URL}/all`);
  }

  updateReservation(idReservation: number, reservationData: any): Observable<ReservationResponse> {

    return this.http.put<ReservationResponse>(`${this.API_URL}/${idReservation}`, reservationData);
  }

  deleteReservation(idReservation: number): Observable<void> {

    return this.http.delete<void>(`${this.API_URL}/${idReservation}`);
  }
}
