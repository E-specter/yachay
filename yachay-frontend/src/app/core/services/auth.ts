import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import {
  AuthUser,
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  ResetPasswordRequest,
} from '../models/auth.models';

import { API_URL } from '../config/api.config';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly platformId = inject(PLATFORM_ID);

  private readonly isBrowser = isPlatformBrowser(this.platformId);

  private readonly tokenKey = 'yachay_token';
  private readonly userKey = 'yachay_user';

  private readonly userSignal = signal<AuthUser | null>(this.getStoredUser());

  readonly user = this.userSignal.asReadonly();
  readonly isAuthenticated = computed(() => !!this.getToken());

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/auth/login`, payload).pipe(
      tap((response) => this.persistSession(response)),
    );
  }

  forgotPassword(payload: ForgotPasswordRequest): Observable<void> {
    return this.http.post<void>(`${API_URL}/auth/forgot-password`, payload);
  }

  resetPassword(payload: ResetPasswordRequest): Observable<void> {
    return this.http.post<void>(`${API_URL}/auth/reset-password`, payload);
  }

  logout(): void {
    if (!this.isBrowser) return;

    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.userSignal.set(null);
  }

  getToken(): string | null {
    if (!this.isBrowser) return null;

    return localStorage.getItem(this.tokenKey);
  }

  private getStoredUser(): AuthUser | null {
    if (!this.isBrowser) return null;

    const rawUser = localStorage.getItem(this.userKey);

    if (!rawUser) return null;

    try {
      return JSON.parse(rawUser) as AuthUser;
    } catch {
      localStorage.removeItem(this.userKey);
      return null;
    }
  }

  private persistSession(response: LoginResponse): void {
    if (!this.isBrowser) return;

    localStorage.setItem(this.tokenKey, response.token);
    localStorage.setItem(this.userKey, JSON.stringify(response.user));
    this.userSignal.set(response.user);
  }
}
