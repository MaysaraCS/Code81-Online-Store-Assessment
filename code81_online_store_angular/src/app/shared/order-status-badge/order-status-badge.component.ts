import { Component, Input } from '@angular/core';
import { OrderStatus } from '../../core/models/order.model';

@Component({
  selector: 'app-order-status-badge',
  standalone: true,
  template: `<span class="badge" [class]="badgeClass">{{ status }}</span>`
})
export class OrderStatusBadgeComponent {
  @Input({ required: true }) status!: OrderStatus;

  get badgeClass(): string {
    switch (this.status) {
      case 'DELIVERED':
      case 'PAID':
        return 'badge--accent';
      case 'CANCELLED':
        return 'badge--danger';
      default:
        return 'badge--neutral'; // PLACED, SHIPPED
    }
  }
}
