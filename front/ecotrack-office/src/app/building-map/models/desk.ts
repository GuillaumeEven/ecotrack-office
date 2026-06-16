/**
 * Desk Model
 * Matches backend DeskResponseDto
 */
export interface Desk {
  id: number;
  name: string;
  status: string;
  isActive: boolean;
  equipmentList: string;
  roomId: number;
}
