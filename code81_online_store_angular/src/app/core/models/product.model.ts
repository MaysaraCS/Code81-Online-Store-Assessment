export interface ProductRequest {
  sku: string;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  categoryId: number;
}

export interface ProductResponse {
  id: number;
  sku: string;
  name: string;
  description?: string;
  price: number;
  stockQuantity: number;
  categoryId: number;
  categoryName: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface StockAdjustmentRequest {
  delta: number;
}

export interface ProductFilter {
  categoryId?: number;
  active?: boolean;
  search?: string;
  minPrice?: number;
  maxPrice?: number;
}
