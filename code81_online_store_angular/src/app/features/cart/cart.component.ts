import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { CartResponse } from '../../core/models/cart.model';
import { ApiError } from '../../core/models/common.model';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent implements OnInit {
  private cartService = inject(CartService);
  private router = inject(Router);

  readonly cart = signal<CartResponse | null>(null);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.cartService.getCart().subscribe(cart => {
      this.cart.set(cart);
      this.loading.set(false);
    });
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity < 1) return;
    this.errorMessage.set(null);
    this.cartService.updateItem(productId, { quantity }).subscribe({
      next: cart => this.cart.set(cart),
      error: (err: { error: ApiError }) => this.errorMessage.set(err.error?.message ?? 'Could not update quantity.')
    });
  }

  removeItem(productId: number): void {
    this.cartService.removeItem(productId).subscribe(cart => this.cart.set(cart));
  }

  clearCart(): void {
    if (!confirm('Empty your cart?')) return;
    this.cartService.clear().subscribe(() => this.load());
  }

  goToCheckout(): void {
    this.router.navigate(['/checkout']);
  }
}
