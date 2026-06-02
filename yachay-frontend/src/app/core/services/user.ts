import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  AdminUser,
  CreateAdminUserRequest,
  ResetAdminUserPasswordResponse,
  UpdateAdminUserRequest,
  UpdateAdminUserStatusRequest,
} from '../models/user.models';

const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly http = inject(HttpClient);

  getUsers(): Observable<AdminUser[]> {
    return this.http.get<AdminUser[]>(`${API_URL}/admin/usuarios`);
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
}
