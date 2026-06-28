/**
 * Desk Model
 * Matches backend DeskResponseDto
 */
export interface DeskModel {
  id: number;
  name: string;
  status: string;
  isActive: boolean;
  equipmentList: string;
  roomId: number;
}
