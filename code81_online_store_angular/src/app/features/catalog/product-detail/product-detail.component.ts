import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { ProductResponse } from '../../../core/models/product.model';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.css'
})
export class ProductDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private productService = inject(ProductService);
  private cartService = inject(CartService);
  protected authService = inject(AuthService);

  readonly product = signal<ProductResponse | null>(null);
  readonly quantity = signal(1);
  readonly message = signal<string | null>(null);
  readonly messageIsError = signal(false);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getById(id).subscribe(p => this.product.set(p));
  }

  addToCart(): void {
    const product = this.product();
    if (!product) return;

    this.cartService.addItem({ productId: product.id, quantity: this.quantity() }).subscribe({
      next: () => {
        this.messageIsError.set(false);
        this.message.set(`Added ${this.quantity()} × "${product.name}" to your cart.`);
      },
      error: (err: { error: ApiError }) => {
        this.messageIsError.set(true);
        this.message.set(err.error?.message ?? 'Could not add to cart.');
      }
    });
  }
}
