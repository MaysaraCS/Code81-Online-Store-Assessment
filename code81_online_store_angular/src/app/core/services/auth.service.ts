import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RefreshRequest } from '../models/auth.model';

const ACCESS_TOKEN_KEY = 'auth_access_token';
const REFRESH_TOKEN_KEY = 'auth_refresh_token';
const ROLE_KEY = 'auth_role';
const USER_ID_KEY = 'auth_user_id';

/**
 * Tokens live in localStorage so a page refresh doesn't force a re-login.
 * This is the simple, common approach - the trade-off (worth knowing, not
 * necessarily worth solving here) is that anything with JS execution on the
 * page (e.g. a malicious dependency) could technically read localStorage.
 * A more locked-down setup would keep the access token in memory only and
 * rely on an httpOnly cookie for the refresh token - more moving parts than
 * this project needs.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;

  // Signals so components can react to login/logout without manually subscribing.
  readonly isAuthenticated = signal<boolean>(this.hasStoredToken());
  readonly role = signal<string | null>(localStorage.getItem(ROLE_KEY));

  constructor(private http: HttpClient) {}

  loginCustomer(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/customers/login`, request)
      .pipe(tap(res => this.storeSession(res)));
  }

  loginStaff(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/staff/login`, request)
      .pipe(tap(res => this.storeSession(res)));
  }

  refresh(): Observable<AuthResponse> {
    const body: RefreshRequest = { refreshToken: this.getRefreshToken() ?? '' };
    return this.http.post<AuthResponse>(`${this.baseUrl}/refresh`, body)
      .pipe(tap(res => this.storeSession(res)));
  }

  logout(): void {
    const refreshToken = this.getRefreshToken();
    if (refreshToken) {
      // Fire-and-forget: even if this network call fails, we still clear
      // local state below so the user is logged out client-side regardless.
      this.http.post(`${this.baseUrl}/logout`, { refreshToken }).subscribe({ error: () => {} });
    }
    this.clearSession();
  }

  getAccessToken(): string | null {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  }

  getUserId(): number | null {
    const raw = localStorage.getItem(USER_ID_KEY);
    return raw ? Number(raw) : null;
  }

  isStaff(): boolean {
    const r = this.role();
    return r === 'ADMIN' || r === 'STORE_MANAGER' || r === 'SUPPORT_AGENT';
  }

  isCustomer(): boolean {
    return this.role() === 'CUSTOMER';
  }

  private storeSession(res: AuthResponse): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, res.accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, res.refreshToken);
    localStorage.setItem(ROLE_KEY, res.role);
    localStorage.setItem(USER_ID_KEY, String(res.userId));
    this.isAuthenticated.set(true);
    this.role.set(res.role);
  }

  private clearSession(): void {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(ROLE_KEY);
    localStorage.removeItem(USER_ID_KEY);
    this.isAuthenticated.set(false);
    this.role.set(null);
  }

  private hasStoredToken(): boolean {
    return !!localStorage.getItem(ACCESS_TOKEN_KEY);
  }
}
