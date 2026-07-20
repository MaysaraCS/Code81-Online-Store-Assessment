import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  ChangePasswordRequest,
  CustomerProfileUpdateRequest,
  CustomerRegisterRequest,
  CustomerResponse
} from '../models/customer.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private readonly baseUrl = `${environment.apiUrl}/customers`;

  constructor(private http: HttpClient) {}

  register(request: CustomerRegisterRequest): Observable<CustomerResponse> {
    return this.http.post<CustomerResponse>(`${this.baseUrl}/register`, request);
  }

  getMyProfile(): Observable<CustomerResponse> {
    return this.http.get<CustomerResponse>(`${this.baseUrl}/me`);
  }

  updateMyProfile(request: CustomerProfileUpdateRequest): Observable<CustomerResponse> {
    return this.http.put<CustomerResponse>(`${this.baseUrl}/me`, request);
  }

  changePassword(request: ChangePasswordRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/me/change-password`, request);
  }
}
