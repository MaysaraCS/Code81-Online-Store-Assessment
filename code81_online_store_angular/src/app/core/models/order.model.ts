export type OrderStatus = 'PLACED' | 'PAID' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface PlaceOrderRequest {
  addressId: number;
}

export interface OrderItemResponse {
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export interface OrderResponse {
  id: number;
  customerId: number;
  status: OrderStatus;
  totalAmount: number;
  shippingAddressId: number;
  items: OrderItemResponse[];
  createdAt: string;
  updatedAt: string;
}

export interface OrderStatusUpdateRequest {
  status: OrderStatus;
}
