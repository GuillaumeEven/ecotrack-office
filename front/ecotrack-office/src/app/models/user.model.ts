export type Role = 'ADMIN' | 'USER';

export interface UserResponse {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  organizationId: number;
  isActive: boolean;
  consentGiven: boolean;
  preferencesJson: string | null;
  createdAt: string;
}

export interface UserMeRequest {
  firstName: string;
  lastName: string;
  email: string;
  consentGiven?: boolean;
  preferencesJson?: string | null;
}
