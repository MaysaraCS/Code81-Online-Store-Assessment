import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-admin-category-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './category-form.component.html',
  styleUrl: './category-form.component.css'
})
export class AdminCategoryFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private categoryService = inject(CategoryService);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly categoryId = signal<number | null>(null);

  readonly form = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['']
  });

  get isEditMode(): boolean {
    return this.categoryId() !== null;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.categoryId.set(id);
      this.categoryService.getById(id).subscribe(c => {
        this.form.patchValue({ name: c.name, description: c.description });
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
    const request = { name: value.name!, description: value.description || undefined };

    const save$ = this.isEditMode
      ? this.categoryService.update(this.categoryId()!, request)
      : this.categoryService.create(request);

    save$.subscribe({
      next: () => this.router.navigate(['/admin/categories']),
      error: (err: { error: ApiError }) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not save category.');
      }
    });
  }
}
