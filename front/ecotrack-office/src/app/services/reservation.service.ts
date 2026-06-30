import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  ReservationResponseModel,
  ReservationResponseWithNameModel
} from '@models/reservation.model';
import { environment } from '@environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ReservationService {

  private http = inject(HttpClient);

  private readonly BASE_URL = `${environment.apiUrl}/reservations`;


  getReservationsByUser(): Observable<ReservationResponseModel[]> {

    return this.http.get<ReservationResponseModel[]>(`${this.BASE_URL}/user`);
  }


  getAllReservations(): Observable<ReservationResponseWithNameModel[]> {

    return this.http.get<ReservationResponseWithNameModel[]>(`${this.BASE_URL}/all`);
  }

  create(userId: number, resourceId: number, date: string): Observable<ReservationResponseModel> {
    const url = this.BASE_URL;
    const payload = {
      date,
      status: 'CONFIRMED',
      userId,
      resourceId
    };
    return this.http.post<ReservationResponseModel>(this.BASE_URL, payload);
  }

  updateReservation(idReservation: number, reservationData: any): Observable<ReservationResponseModel> {

    return this.http.put<ReservationResponseModel>(`${this.BASE_URL}/${idReservation}`, reservationData);
  }

  deleteReservation(idReservation: number): Observable<void> {

    return this.http.delete<void>(`${this.BASE_URL}/${idReservation}`);
  }
}
