import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ProductService } from '../../../core/services/product.service';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryResponse } from '../../../core/models/category.model';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-admin-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css'
})
export class AdminProductFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private productService = inject(ProductService);
  private categoryService = inject(CategoryService);

  readonly categories = signal<CategoryResponse[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly productId = signal<number | null>(null);

  readonly form = this.fb.group({
    sku: ['', [Validators.required, Validators.maxLength(50)]],
    name: ['', [Validators.required, Validators.maxLength(150)]],
    description: [''],
    price: [0, [Validators.required, Validators.min(0.01)]],
    stockQuantity: [0, [Validators.required, Validators.min(0)]],
    categoryId: [null as number | null, [Validators.required]]
  });

  get isEditMode(): boolean {
    return this.productId() !== null;
  }

  ngOnInit(): void {
    this.categoryService.list().subscribe(res => this.categories.set(res.content));

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.productId.set(id);
      this.productService.getById(id).subscribe(p => {
        this.form.patchValue({
          sku: p.sku,
          name: p.name,
          description: p.description,
          price: p.price,
          stockQuantity: p.stockQuantity,
          categoryId: p.categoryId
        });
      });
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const value = this.form.getRawValue();
    const request = {
      sku: value.sku!,
      name: value.name!,
      description: value.description || undefined,
      price: value.price!,
      stockQuantity: value.stockQuantity!,
      categoryId: value.categoryId!
    };

    const save$ = this.isEditMode
      ? this.productService.update(this.productId()!, request)
      : this.productService.create(request);

    save$.subscribe({
      next: () => this.router.navigate(['/admin/products']),
      error: (err: { error: ApiError }) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not save product.');
      }
    });
  }
}
