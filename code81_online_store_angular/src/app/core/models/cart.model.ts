export interface CartItemRequest {
  productId: number;
  quantity: number;
}

export interface CartItemUpdateRequest {
  quantity: number;
}

export interface CartItemResponse {
  id: number;
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
  availableStock: number;
}

export interface CartResponse {
  id: number;
  items: CartItemResponse[];
  totalAmount: number;
}
