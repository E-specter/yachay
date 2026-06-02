import { isPlatformBrowser } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, PLATFORM_ID, computed, inject, signal } from '@angular/core';
import { Observable, catchError, of, tap, throwError } from 'rxjs';

import {
  AuthUser,
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  ResetPasswordRequest,
} from '../models/auth.models';

const API_URL = 'http://localhost:8080/api';
const DEV_USERS: readonly {
  email: string;
  password: string;
  response: LoginResponse;
}[] = [
  {
    email: 'admin@yachay.edu.pe',
    password: 'Admin123456',
    response: {
      token: 'dev-admin-token',
      user: {
        id: 1,
        nombres: 'Administrador',
        apellidos: 'Yachay',
        email: 'admin@yachay.edu.pe',
        role: 'ADMINISTRADOR',
      },
    },
  },
  {
    email: 'admin@mgp.edu.pe',
    password: 'admin123',
    response: {
      token: 'dev-admin-token',
      user: {
        id: 1,
        nombres: 'Administrador',
        apellidos: 'Yachay',
        email: 'admin@mgp.edu.pe',
        role: 'ADMINISTRADOR',
      },
    },
  },
  {
    email: 'docente@yachay.edu.pe',
    password: 'Docente123456',
    response: {
      token: 'dev-teacher-token',
      user: {
        id: 2,
        nombres: 'Rosa Elena',
        apellidos: 'Vargas Medina',
        email: 'docente@yachay.edu.pe',
        role: 'DOCENTE',
      },
    },
  },
  {
    email: 'alumno@yachay.edu.pe',
    password: 'Alumno123456',
    response: {
      token: 'dev-student-token',
      user: {
        id: 3,
        nombres: 'María Fernanda',
        apellidos: 'Salazar Rojas',
        email: 'alumno@yachay.edu.pe',
        role: 'ALUMNO',
      },
    },
  },
];

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);

  private readonly isBrowser = isPlatformBrowser(this.platformId);

  private readonly tokenKey = 'yachay_token';
  private readonly userKey = 'yachay_user';

  private userSignal = signal<AuthUser | null>(this.getStoredUser());

  user = this.userSignal.asReadonly();
  isAuthenticated = computed(() => !!this.getToken());

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${API_URL}/auth/login`, payload).pipe(
      catchError((error: unknown) => this.handleDevLoginFallback(payload, error)),
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

  private handleDevLoginFallback(
    payload: LoginRequest,
    error: unknown,
  ): Observable<LoginResponse> {
    const devUser = this.findDevUser(payload);

    if (!this.isApiUnavailable(error) || !devUser) {
      return throwError(() => error);
    }

    return of(devUser.response);
  }

  private isApiUnavailable(error: unknown): boolean {
    return error instanceof HttpErrorResponse && error.status === 0;
  }

  private findDevUser(payload: LoginRequest): (typeof DEV_USERS)[number] | undefined {
    const email = payload.email.trim().toLowerCase();

    return DEV_USERS.find(
      (user) => user.email === email && user.password === payload.password,
    );
  }
}
