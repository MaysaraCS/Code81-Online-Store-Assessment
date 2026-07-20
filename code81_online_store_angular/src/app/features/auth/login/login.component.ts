import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  // 'customer' vs 'staff' picks which AuthService method (and therefore
  // which backend endpoint) the form submits to - see onSubmit().
  readonly accountType = signal<'customer' | 'staff'>('customer');
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  setAccountType(type: 'customer' | 'staff'): void {
    this.accountType.set(type);
    this.errorMessage.set(null);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const request = this.form.getRawValue() as { email: string; password: string };
    const login$ = this.accountType() === 'customer'
      ? this.authService.loginCustomer(request)
      : this.authService.loginStaff(request);

    login$.subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/']);
      },
      error: (err: { error: ApiError }) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Login failed. Please try again.');
      }
    });
  }
}
