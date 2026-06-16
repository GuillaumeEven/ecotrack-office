/**
 * User Model
 * Represents a user in the system
 */
export interface User {
  id: string | number;
  email: string;
  firstName: string;
  lastName: string;
  role: 'ADMIN' | 'FACILITY_MANAGER' | 'EMPLOYEE';
  department?: string;
  createdAt?: Date;
  updatedAt?: Date;
}
