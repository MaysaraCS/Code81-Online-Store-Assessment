import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerService } from '../../../core/services/customer.service';
import { AuthService } from '../../../core/services/auth.service';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private customerService = inject(CustomerService);
  private authService = inject(AuthService);
  private router = inject(Router);

  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.group({
    firstName: ['', [Validators.required, Validators.maxLength(60)]],
    lastName: ['', [Validators.required, Validators.maxLength(60)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    phone: ['']
  });

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);

    const { firstName, lastName, email, password, phone } = this.form.getRawValue();

    this.customerService.register({
      firstName: firstName!,
      lastName: lastName!,
      email: email!,
      password: password!,
      phone: phone || undefined
    }).subscribe({
      next: () => {
        // Registration doesn't log the person in by itself (the backend
        // endpoint just creates the account) - so we log in right after,
        // using the same credentials they just typed, for a smooth flow.
        this.authService.loginCustomer({ email: email!, password: password! }).subscribe({
          next: () => {
            this.loading.set(false);
            this.router.navigate(['/']);
          },
          error: () => {
            this.loading.set(false);
            this.router.navigate(['/login']);
          }
        });
      },
      error: (err: { error: ApiError }) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Registration failed. Please try again.');
      }
    });
  }
}
