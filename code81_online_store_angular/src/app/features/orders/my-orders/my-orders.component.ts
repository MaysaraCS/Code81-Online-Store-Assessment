import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { OrderResponse } from '../../../core/models/order.model';
import { OrderStatusBadgeComponent } from '../../../shared/order-status-badge/order-status-badge.component';

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule, RouterLink, OrderStatusBadgeComponent],
  templateUrl: './my-orders.component.html',
  styleUrl: './my-orders.component.css'
})
export class MyOrdersComponent implements OnInit {
  private orderService = inject(OrderService);

  readonly orders = signal<OrderResponse[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.orderService.listMine(0, 50).subscribe(res => {
      this.orders.set(res.content);
      this.loading.set(false);
    });
  }
}
