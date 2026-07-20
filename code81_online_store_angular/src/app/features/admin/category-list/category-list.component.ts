import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CategoryService } from '../../../core/services/category.service';
import { CategoryResponse } from '../../../core/models/category.model';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-admin-category-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './category-list.component.html',
  styleUrl: './category-list.component.css'
})
export class AdminCategoryListComponent implements OnInit {
  private categoryService = inject(CategoryService);

  readonly categories = signal<CategoryResponse[]>([]);
  readonly loading = signal(true);
  readonly errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.categoryService.list().subscribe(res => {
      this.categories.set(res.content);
      this.loading.set(false);
    });
  }

  delete(category: CategoryResponse): void {
    if (!confirm(`Delete category "${category.name}"?`)) return;
    this.errorMessage.set(null);
    this.categoryService.delete(category.id).subscribe({
      next: () => this.load(),
      // Most common failure here: 409 because products still reference it -
      // the backend blocks that on purpose, see backend README.
      error: (err: { error: ApiError }) => this.errorMessage.set(err.error?.message ?? 'Could not delete category.')
    });
  }
}
