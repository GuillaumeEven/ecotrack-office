/**
 * Desk Model
 * Represents a desk or workspace within a room
 */
export interface Desk {
  id: string | number;
  roomId: string | number;
  name: string;
  displayOrder: number;
  status: 'AVAILABLE' | 'UNAVAILABLE' | 'MAINTENANCE';
  type?: string; // e.g., 'standing_desk', 'regular_desk', 'workstation'
  position?: string; // e.g., grid coordinates like "A1", "B2"
  description?: string;
  createdAt?: Date;
  updatedAt?: Date;
}
