/**
 * Room Model
 * Matches backend RoomResponseDto
 */
export interface Room {
  id: number;
  name: string;
  status: string;
  isActive: boolean;
  equipmentList: string;
  roomType: string;
  surfaceArea: number;
  floorId: number;
  capacity: number;
}
