/**
 * Room Model
 * Matches backend RoomResponseDto
 */
export interface RoomModel {
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
