import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CartItemRequest, CartItemUpdateRequest, CartResponse } from '../models/cart.model';

@Injectable({ providedIn: 'root' })
export class CartService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/cart`;

  getCart(): Observable<CartResponse> {
    return this.http.get<CartResponse>(this.baseUrl);
  }

  addItem(request: CartItemRequest): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.baseUrl}/items`, request);
  }

  updateItem(productId: number, request: CartItemUpdateRequest): Observable<CartResponse> {
    return this.http.put<CartResponse>(`${this.baseUrl}/items/${productId}`, request);
  }

  removeItem(productId: number): Observable<CartResponse> {
    return this.http.delete<CartResponse>(`${this.baseUrl}/items/${productId}`);
  }

  clear(): Observable<void> {
    return this.http.delete<void>(this.baseUrl);
  }
}
