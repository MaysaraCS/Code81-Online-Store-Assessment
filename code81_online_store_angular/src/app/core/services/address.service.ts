import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AddressRequest, AddressResponse } from '../models/address.model';

@Injectable({ providedIn: 'root' })
export class AddressService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/customers/me/addresses`;

  list(): Observable<AddressResponse[]> {
    return this.http.get<AddressResponse[]>(this.baseUrl);
  }

  add(request: AddressRequest): Observable<AddressResponse> {
    return this.http.post<AddressResponse>(this.baseUrl, request);
  }

  update(id: number, request: AddressRequest): Observable<AddressResponse> {
    return this.http.put<AddressResponse>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
