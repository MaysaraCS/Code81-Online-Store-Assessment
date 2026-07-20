import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ProductFilter, ProductRequest, ProductResponse, StockAdjustmentRequest } from '../models/product.model';
import { PageResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/products`;

  list(filter: ProductFilter = {}, page = 0, size = 20): Observable<PageResponse<ProductResponse>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (filter.categoryId != null) params = params.set('categoryId', filter.categoryId);
    if (filter.active != null) params = params.set('active', filter.active);
    if (filter.search) params = params.set('search', filter.search);
    if (filter.minPrice != null) params = params.set('minPrice', filter.minPrice);
    if (filter.maxPrice != null) params = params.set('maxPrice', filter.maxPrice);
    return this.http.get<PageResponse<ProductResponse>>(this.baseUrl, { params });
  }

  getById(id: number): Observable<ProductResponse> {
    return this.http.get<ProductResponse>(`${this.baseUrl}/${id}`);
  }

  create(request: ProductRequest): Observable<ProductResponse> {
    return this.http.post<ProductResponse>(this.baseUrl, request);
  }

  update(id: number, request: ProductRequest): Observable<ProductResponse> {
    return this.http.put<ProductResponse>(`${this.baseUrl}/${id}`, request);
  }

  adjustStock(id: number, request: StockAdjustmentRequest): Observable<ProductResponse> {
    return this.http.patch<ProductResponse>(`${this.baseUrl}/${id}/stock`, request);
  }

  deactivate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
