/**
 * Floor Model
 * Matches backend FloorResponseDto
 */
export interface FloorModel {
  id: number;
  level: number;
  isActive: boolean;
  organizationId: number;
  name?: string;
}
