import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AddressService } from '../../core/services/address.service';
import { OrderService } from '../../core/services/order.service';
import { CartService } from '../../core/services/cart.service';
import { AddressResponse } from '../../core/models/address.model';
import { CartResponse } from '../../core/models/cart.model';
import { ApiError } from '../../core/models/common.model';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent implements OnInit {
  private addressService = inject(AddressService);
  private orderService = inject(OrderService);
  private cartService = inject(CartService);
  private router = inject(Router);

  readonly addresses = signal<AddressResponse[]>([]);
  readonly cart = signal<CartResponse | null>(null);
  readonly selectedAddressId = signal<number | null>(null);
  readonly loading = signal(true);
  readonly placing = signal(false);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.addressService.list().subscribe(addresses => {
      this.addresses.set(addresses);
      const defaultAddress = addresses.find(a => a.isDefault) ?? addresses[0];
      if (defaultAddress) this.selectedAddressId.set(defaultAddress.id);
    });

    this.cartService.getCart().subscribe(cart => {
      this.cart.set(cart);
      this.loading.set(false);
    });
  }

  placeOrder(): void {
    const addressId = this.selectedAddressId();
    if (!addressId) {
      this.errorMessage.set('Please select a shipping address.');
      return;
    }

    this.placing.set(true);
    this.errorMessage.set(null);

    this.orderService.placeOrder({ addressId }).subscribe({
      next: order => this.router.navigate(['/orders', order.id]),
      error: (err: { error: ApiError }) => {
        this.placing.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not place order.');
      }
    });
  }
}
