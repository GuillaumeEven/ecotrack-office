import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ReservationService } from './reservation.service';
import { environment } from '@environments/environment';
import { firstValueFrom } from 'rxjs';

describe('ReservationService', () => {
  let service: ReservationService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReservationService]
    }).compileComponents();

    service = TestBed.inject(ReservationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  /**
   * Test: ReservationService.getReservationsByUser()
   *
   * Dado: usuario autenticado
   * Cuando: se llama getReservationsByUser()
   * Entonces: retorna Observable con lista de reservaciones del usuario
   */
  it('should fetch user reservations from API', async () => {
    const mockReservations = [
      { id: 1, date: '2026-06-28', status: 'CONFIRMED', createdAt: '2026-06-28T10:00:00Z', userId: 1, resourceName: 'Desk A-001', resourceEquipmentList: 'Monitor,Keyboard' },
      { id: 2, date: '2026-06-29', status: 'PENDING', createdAt: '2026-06-29T10:00:00Z', userId: 1, resourceName: 'Desk A-002', resourceEquipmentList: 'Monitor' }
    ];

    const promise = firstValueFrom(service.getReservationsByUser());

    const req = httpMock.expectOne(`${environment.apiUrl}/reservations/user`);
    expect(req.request.method).toBe('GET');
    req.flush(mockReservations);

    const reservations = await promise;
    expect(reservations).toEqual(mockReservations);
    expect(reservations.length).toBe(2);
  });

  /**
   * Test: ReservationService.getAllReservations()
   *
   * Dado: usuario ADMIN
   * Cuando: se llama getAllReservations()
   * Entonces: retorna Observable con todas las reservaciones (con nombres)
   */
  it('should fetch all reservations (admin endpoint)', async () => {
    const mockAllReservations = [
      { id: 1, date: '2026-06-28', status: 'CONFIRMED', createdAt: '2026-06-28T10:00:00Z', userId: 1, userFullName: 'Admin User', resourceName: 'Desk A-001', resourceEquipmentList: 'Monitor,Keyboard' },
      { id: 2, date: '2026-06-29', status: 'CONFIRMED', createdAt: '2026-06-29T10:00:00Z', userId: 2, userFullName: 'Regular User', resourceName: 'Desk A-002', resourceEquipmentList: 'Monitor' }
    ];

    const promise = firstValueFrom(service.getAllReservations());

    const req = httpMock.expectOne(`${environment.apiUrl}/reservations/all`);
    expect(req.request.method).toBe('GET');
    req.flush(mockAllReservations);

    const reservations = await promise;
    expect(reservations).toEqual(mockAllReservations);
    expect(reservations.length).toBe(2);
  });

  /**
   * Test: ReservationService.create()
   *
   * Dado: usuario quiere crear una reservación para un desk en cierta fecha
   * Cuando: se llama create(userId, resourceId, date)
   * Entonces: envía POST con payload correcto y retorna la reservación creada
   */
  it('should create a new reservation with correct payload', async () => {
    const userId = 1;
    const resourceId = 10;
    const date = '2026-06-28';

    const mockCreatedReservation = {
      id: 99,
      date: '2026-06-28',
      status: 'CONFIRMED',
      createdAt: '2026-06-28T10:00:00Z',
      userId: 1,
      resourceName: 'Desk A-001',
      resourceEquipmentList: 'Monitor'
    };

    const promise = firstValueFrom(service.create(userId, resourceId, date));

    const req = httpMock.expectOne(`${environment.apiUrl}/reservations`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({
      date,
      status: 'CONFIRMED',
      userId,
      resourceId
    });
    req.flush(mockCreatedReservation);

    const reservation = await promise;
    expect(reservation).toEqual(mockCreatedReservation);
    expect(reservation.id).toBe(99);
    expect(reservation.status).toBe('CONFIRMED');
  });

  /**
   * Test: ReservationService.updateReservation()
   *
   * Dado: una reservación existente que se quiere modificar
   * Cuando: se llama updateReservation(id, datos)
   * Entonces: envía PUT con los datos y retorna la reservación actualizada
   */
  it('should update an existing reservation', async () => {
    const reservationId = 1;
    const updateData = { status: 'CANCELLED' };
    const mockUpdatedReservation = {
      id: 1,
      date: '2026-06-28',
      status: 'CANCELLED',
      createdAt: '2026-06-28T10:00:00Z',
      userId: 1,
      resourceName: 'Desk A-001',
      resourceEquipmentList: 'Monitor'
    };

    const promise = firstValueFrom(service.updateReservation(reservationId, updateData));

    const req = httpMock.expectOne(`${environment.apiUrl}/reservations/${reservationId}`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateData);
    req.flush(mockUpdatedReservation);

    const reservation = await promise;
    expect(reservation.status).toBe('CANCELLED');
  });

  /**
   * Test: ReservationService.deleteReservation()
   *
   * Dado: una reservación existente
   * Cuando: se llama deleteReservation(id)
   * Entonces: envía DELETE y retorna void
   */
  it('should delete a reservation', async () => {
    const reservationId = 1;
    const promise = firstValueFrom(service.deleteReservation(reservationId));

    const req = httpMock.expectOne(`${environment.apiUrl}/reservations/${reservationId}`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);

    await promise;
    expect(true).toBe(true);
  });
});
