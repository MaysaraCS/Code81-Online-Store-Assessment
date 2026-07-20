import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { OrderResponse, OrderStatus, OrderStatusUpdateRequest, PlaceOrderRequest } from '../models/order.model';
import { PageResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/orders`;

  placeOrder(request: PlaceOrderRequest): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.baseUrl, request);
  }

  getById(id: number): Observable<OrderResponse> {
    return this.http.get<OrderResponse>(`${this.baseUrl}/${id}`);
  }

  listMine(page = 0, size = 20): Observable<PageResponse<OrderResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<OrderResponse>>(`${this.baseUrl}/me`, { params });
  }

  listAll(status: OrderStatus | null, page = 0, size = 20): Observable<PageResponse<OrderResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (status) params = params.set('status', status);
    return this.http.get<PageResponse<OrderResponse>>(this.baseUrl, { params });
  }

  updateStatus(id: number, request: OrderStatusUpdateRequest): Observable<OrderResponse> {
    return this.http.patch<OrderResponse>(`${this.baseUrl}/${id}/status`, request);
  }
}
