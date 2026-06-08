/**
 * API Configuration
 * Centralized endpoint configuration for assets management
 */

export const API_CONFIG = {
  baseUrl: 'http://localhost:8080/api/v1',
  organizationId: 1, // TODO: Get from user/session after auth is implemented
  endpoints: {
    floors: '/floors',
    rooms: '/rooms',
    desks: '/desks'
  }
};
