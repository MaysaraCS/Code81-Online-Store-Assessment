import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { CartService } from '../../../core/services/cart.service';
import { AuthService } from '../../../core/services/auth.service';
import { ProductResponse } from '../../../core/models/product.model';
import { CategoryResponse } from '../../../core/models/category.model';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  private categoryService = inject(CategoryService);
  private cartService = inject(CartService);
  protected authService = inject(AuthService);

  readonly products = signal<ProductResponse[]>([]);
  readonly categories = signal<CategoryResponse[]>([]);
  readonly loading = signal(true);
  readonly page = signal(0);
  readonly totalPages = signal(1);
  readonly selectedCategoryId = signal<number | null>(null);
  readonly search = signal('');
  readonly addedMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.categoryService.list().subscribe(res => this.categories.set(res.content));
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.productService
      .list(
        { categoryId: this.selectedCategoryId() ?? undefined, active: true, search: this.search() || undefined },
        this.page(),
        12
      )
      .subscribe(res => {
        this.products.set(res.content);
        this.totalPages.set(res.totalPages);
        this.loading.set(false);
      });
  }

  onSearch(value: string): void {
    this.search.set(value);
    this.page.set(0);
    this.loadProducts();
  }

  onCategoryChange(categoryId: string): void {
    this.selectedCategoryId.set(categoryId ? Number(categoryId) : null);
    this.page.set(0);
    this.loadProducts();
  }

  nextPage(): void {
    if (this.page() + 1 < this.totalPages()) {
      this.page.update(p => p + 1);
      this.loadProducts();
    }
  }

  prevPage(): void {
    if (this.page() > 0) {
      this.page.update(p => p - 1);
      this.loadProducts();
    }
  }

  addToCart(product: ProductResponse): void {
    this.addedMessage.set(null);
    this.cartService.addItem({ productId: product.id, quantity: 1 }).subscribe({
      next: () => this.addedMessage.set(`Added "${product.name}" to your cart.`),
      error: (err: { error: ApiError }) => this.addedMessage.set(err.error?.message ?? 'Could not add to cart.')
    });
  }
}
