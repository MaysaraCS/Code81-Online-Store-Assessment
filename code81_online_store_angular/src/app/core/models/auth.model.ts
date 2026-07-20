// Mirrors com.code81.onlinestore.dto.auth on the backend - field names match
// exactly so there's no mental translation needed when reading a network tab.

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
  role: string; // 'CUSTOMER' | 'ADMIN' | 'STORE_MANAGER' | 'SUPPORT_AGENT'
  userId: number;
}

export interface RefreshRequest {
  refreshToken: string;
}
