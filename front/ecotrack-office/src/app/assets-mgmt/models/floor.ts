/**
 * Floor Model
 * Represents a physical floor in the office building
 */
export interface Floor {
  id: string | number;
  name: string;
  displayOrder: number;
  status: 'AVAILABLE' | 'UNAVAILABLE' | 'MAINTENANCE';
  level?: number;
  description?: string;
  totalRooms?: number;
  createdAt?: Date;
  updatedAt?: Date;
}
