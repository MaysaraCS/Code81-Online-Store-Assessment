import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { StaffCreateRequest, StaffResponse, StaffUpdateRequest } from '../models/staff.model';
import { PageResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class StaffService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/staff`;

  list(page = 0, size = 20): Observable<PageResponse<StaffResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<StaffResponse>>(this.baseUrl, { params });
  }

  getById(id: number): Observable<StaffResponse> {
    return this.http.get<StaffResponse>(`${this.baseUrl}/${id}`);
  }

  create(request: StaffCreateRequest): Observable<StaffResponse> {
    return this.http.post<StaffResponse>(this.baseUrl, request);
  }

  update(id: number, request: StaffUpdateRequest): Observable<StaffResponse> {
    return this.http.put<StaffResponse>(`${this.baseUrl}/${id}`, request);
  }
}
