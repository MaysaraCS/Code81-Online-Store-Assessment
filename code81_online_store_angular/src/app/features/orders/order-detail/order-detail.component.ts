import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { PaymentService } from '../../../core/services/payment.service';
import { AuthService } from '../../../core/services/auth.service';
import { OrderResponse, OrderStatus } from '../../../core/models/order.model';
import { ApiError } from '../../../core/models/common.model';
import { OrderStatusBadgeComponent } from '../../../shared/order-status-badge/order-status-badge.component';

// Mirrors OrderServiceImpl.ALLOWED_TRANSITIONS on the backend - used here only
// to decide which buttons to *show*. The backend re-validates every
// transition regardless, so this list going stale would just mean a button
// that returns a 409, not a real bug.
const STAFF_NEXT_STATUSES: Record<OrderStatus, OrderStatus[]> = {
  PLACED: ['PAID', 'CANCELLED'],
  PAID: ['SHIPPED', 'CANCELLED'],
  SHIPPED: ['DELIVERED'],
  DELIVERED: [],
  CANCELLED: []
};

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, OrderStatusBadgeComponent],
  templateUrl: './order-detail.component.html',
  styleUrl: './order-detail.component.css'
})
export class OrderDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private orderService = inject(OrderService);
  private paymentService = inject(PaymentService);
  protected authService = inject(AuthService);

  readonly order = signal<OrderResponse | null>(null);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);
  readonly payingNow = signal(false);
  readonly updatingStatus = signal(false);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.load(id);
  }

  load(id: number): void {
    this.loading.set(true);
    this.orderService.getById(id).subscribe(order => {
      this.order.set(order);
      this.loading.set(false);
    });
  }

  get staffNextStatuses(): OrderStatus[] {
    return STAFF_NEXT_STATUSES[this.order()?.status ?? 'DELIVERED'];
  }

  get canCustomerCancel(): boolean {
    const status = this.order()?.status;
    return status === 'PLACED' || status === 'PAID';
  }

  updateStatus(status: OrderStatus): void {
    const order = this.order();
    if (!order) return;
    if (status === 'CANCELLED' && !confirm('Cancel this order? Stock will be restored.')) return;

    this.updatingStatus.set(true);
    this.errorMessage.set(null);
    this.orderService.updateStatus(order.id, { status }).subscribe({
      next: updated => {
        this.order.set(updated);
        this.updatingStatus.set(false);
      },
      error: (err: { error: ApiError }) => {
        this.updatingStatus.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not update order status.');
      }
    });
  }

  payNow(): void {
    const order = this.order();
    if (!order) return;

    this.payingNow.set(true);
    this.errorMessage.set(null);
    this.paymentService.createCheckoutSession(order.id).subscribe({
      next: session => {
        // Full-page redirect to Stripe's hosted checkout - not an Angular
        // route, so this is a real navigation, not router.navigate().
        window.location.href = session.checkoutUrl;
      },
      error: (err: { error: ApiError }) => {
        this.payingNow.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not start checkout.');
      }
    });
  }
}
