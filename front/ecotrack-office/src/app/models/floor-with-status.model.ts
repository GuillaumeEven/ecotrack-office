/**
 * Floor Status Models
 * DTOs matching backend FloorWithStatusDto, RoomWithStatusDto, DeskWithStatusDto
 */

import { ResourceStatusModel, FloorModel, DeskModel, RoomModel } from './index.model';

/**
 * Desk with calculated status for a specific date
 */
export interface DeskWithStatusModel {
  desk: DeskModel;
  calculatedStatus: ResourceStatusModel; // Calculated status for the desk on the given date
  reservedBy?: string; // email of the user who reserved it
  reservationId?: number; // ID of the reservation (for cancellation)
}

/**
 * Room with all desks and their statuses for a specific date
 */
export interface RoomWithStatusModel {
  room: RoomModel;
  desks: DeskWithStatusModel[];
  occupancyRate: number; // 0.0 - 1.0 (e.g., 0.75 = 75%)
  roomStatus: ResourceStatusModel; // Status of the room itself
  reservedBy?: string; // For meeting rooms: email of user who reserved
  reservationId?: number; // For meeting rooms: ID of the reservation
}

/**
 * Floor with all rooms and their statuses for a specific date
 * This is the main DTO returned by the floor status endpoint
 */
export interface FloorWithStatusModel {
  floor: FloorModel;
  rooms: RoomWithStatusModel[];
  date: string; // YYYY-MM-DD
  desksOccupied: boolean; // Flag: last DESK_AREA of this floor has occupancy >= 80%
  meetingRoomsOccupied: boolean; // Flag: last MEETING_ROOM of this floor is RESERVED
}
