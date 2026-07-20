import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { OrderResponse, OrderStatus } from '../../../core/models/order.model';
import { OrderStatusBadgeComponent } from '../../../shared/order-status-badge/order-status-badge.component';

const STATUS_OPTIONS: OrderStatus[] = ['PLACED', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

@Component({
  selector: 'app-admin-orders',
  standalone: true,
  imports: [CommonModule, RouterLink, OrderStatusBadgeComponent],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css'
})
export class AdminOrdersComponent implements OnInit {
  private orderService = inject(OrderService);

  readonly statusOptions = STATUS_OPTIONS;
  readonly orders = signal<OrderResponse[]>([]);
  readonly loading = signal(true);
  readonly statusFilter = signal<OrderStatus | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.orderService.listAll(this.statusFilter(), 0, 50).subscribe(res => {
      this.orders.set(res.content);
      this.loading.set(false);
    });
  }

  onFilterChange(value: string): void {
    this.statusFilter.set(value ? (value as OrderStatus) : null);
    this.load();
  }
}
