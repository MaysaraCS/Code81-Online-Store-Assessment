import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { ProductResponse } from '../../../core/models/product.model';

@Component({
  selector: 'app-admin-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.css'
})
export class AdminProductListComponent implements OnInit {
  private productService = inject(ProductService);

  readonly products = signal<ProductResponse[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    // Staff see inactive products too, unlike the customer-facing catalog.
    this.productService.list({}, 0, 100).subscribe(res => {
      this.products.set(res.content);
      this.loading.set(false);
    });
  }

  adjustStock(product: ProductResponse, delta: number): void {
    this.productService.adjustStock(product.id, { delta }).subscribe(() => this.load());
  }

  deactivate(product: ProductResponse): void {
    if (!confirm(`Deactivate "${product.name}"? It will be hidden from the customer catalog.`)) return;
    this.productService.deactivate(product.id).subscribe(() => this.load());
  }
}
