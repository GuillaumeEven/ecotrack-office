/**
 * Room Model
 * Represents a room (meeting room, office space, etc.) within a floor
 */
export interface Room {
  id: string | number;
  floorId: string | number;
  name: string;
  displayOrder: number;
  status: 'AVAILABLE' | 'UNAVAILABLE' | 'MAINTENANCE';
  capacity: number;
  assets?: string[]; // IDs of desks/assets in this room
  description?: string;
  type?: string; // e.g., 'meeting_room', 'open_space', 'office'
  createdAt?: Date;
  updatedAt?: Date;
}
