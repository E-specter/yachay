import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map } from 'rxjs';

import {
  AdminUser,
  AdminUserRole,
  CreateAdminUserRequest,
  ResetAdminUserPasswordResponse,
  UpdateAdminUserRequest,
  UpdateAdminUserStatusRequest,
} from '../models/user.models';

const API_URL = 'http://localhost:8080/api';

interface BackendUser {
  id: string | number;
  email: string;
  displayName?: string;
  roleNames?: string[];
  profile?: {
    firstName?: string;
    lastName?: string;
    isActive?: boolean;
  };
  createdAt?: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);

  getUsers(): Observable<AdminUser[]> {
    return this.http.get<BackendUser[]>(`${API_URL}/admin/usuarios`).pipe(
      map((users) => users.map((user) => this.toAdminUser(user))),
    );
  }

  createUser(payload: CreateAdminUserRequest): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${API_URL}/admin/usuarios`, payload);
  }

  updateUser(id: number, payload: UpdateAdminUserRequest): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${API_URL}/admin/usuarios/${id}`, payload);
  }

  updateStatus(id: number, payload: UpdateAdminUserStatusRequest): Observable<AdminUser> {
    return this.http.patch<AdminUser>(`${API_URL}/admin/usuarios/${id}/estado`, payload);
  }

  resetPassword(id: number): Observable<ResetAdminUserPasswordResponse> {
    return this.http.patch<ResetAdminUserPasswordResponse>(
      `${API_URL}/admin/usuarios/${id}/reset-password`,
      {},
    );
  }

  private toAdminUser(user: BackendUser): AdminUser {
    const displayNameParts = (user.displayName ?? '').trim().split(/\s+/).filter(Boolean);
    const firstName = user.profile?.firstName ?? displayNameParts[0] ?? '';
    const lastName = user.profile?.lastName ?? displayNameParts.slice(1).join(' ');

    return {
      id: Number(user.id),
      nombres: firstName,
      apellidos: lastName,
      email: user.email,
      rol: this.toRole(user.roleNames),
      estado: user.profile?.isActive === false ? 'INACTIVO' : 'ACTIVO',
      fechaCreacion: user.createdAt ? user.createdAt.slice(0, 10) : '',
    };
  }

  private toRole(roleNames: string[] | undefined): AdminUserRole {
    const normalized = (roleNames ?? []).map((role) => role.toUpperCase());

    if (normalized.some((role) => role === 'ADMIN' || role === 'ADMINISTRADOR')) {
      return 'ADMINISTRADOR';
    }

    if (normalized.some((role) => role === 'TEACHER' || role === 'PROFESOR' || role === 'DOCENTE')) {
      return 'DOCENTE';
    }

    return 'ALUMNO';
  }
}
