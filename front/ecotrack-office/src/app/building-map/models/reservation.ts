/**
 * Reservation Model
 * Represents a desk reservation/booking in the system
 */
export type Reservation = {
  id: number;
  deskId: number;
  roomId: number;
  floorId: number;
  userId: number;
  startDate: string; // ISO date format: "2026-06-09"
  endDate: string;
  status: 'CONFIRMED' | 'PENDING' | 'CANCELLED';
};
