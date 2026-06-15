/**
 * Floor Status Models
 * DTOs matching backend FloorWithStatusDto, RoomWithStatusDto, DeskWithStatusDto
 */

import { ResourceStatus } from './resource-status';
import { Floor } from './floor';
import { Room } from './room';
import { Desk } from './desk';

/**
 * Desk with calculated status for a specific date
 */
export interface DeskWithStatus {
  desk: Desk;
  calculatedStatus: ResourceStatus;
  reservedBy?: string; // email of the user who reserved it
  reservationId?: number; // ID of the reservation (for cancellation)
}

/**
 * Room with all desks and their statuses for a specific date
 */
export interface RoomWithStatus {
  room: Room;
  desks: DeskWithStatus[];
  occupancyRate: number; // 0.0 - 1.0 (e.g., 0.75 = 75%)
  roomStatus: ResourceStatus; // Status of the room itself
}

/**
 * Floor with all rooms and their statuses for a specific date
 * This is the main DTO returned by the floor status endpoint
 */
export interface FloorWithStatus {
  floor: Floor;
  rooms: RoomWithStatus[];
  date: string; // YYYY-MM-DD
  desksOccupied: boolean; // Flag: last DESK_AREA of this floor has occupancy >= 80%
  meetingRoomsOccupied: boolean; // Flag: last MEETING_ROOM of this floor is RESERVED
}
