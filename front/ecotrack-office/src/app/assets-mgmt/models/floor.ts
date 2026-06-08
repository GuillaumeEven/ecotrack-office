/**
 * Floor Model
 * Matches backend FloorResponseDto
 */
export interface Floor {
  id: number;
  level: number;
  isActive: boolean;
  organizationId: number;
}
