// Mapea LoginRequestDto de Spring
export interface LoginRequest {
  email: string;
  password: string;
}

// Mapea LoginResponseDto de Spring
export interface LoginResponse {
  token: string;
  userId: number;
  role: string;
}
