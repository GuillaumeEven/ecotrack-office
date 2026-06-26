export type IncidentStatus = 'IN_PROGRESS' | 'RESOLVED';

export interface IncidentRequest {
  description: string;
  resourceId: number;
  userId: number;
}

export interface IncidentResponse {
  id: number;
  description: string;
  status: IncidentStatus;
  createdAt: string;
  resolvedAt: string | null;
  userId: number;
  resourceId: number;
}