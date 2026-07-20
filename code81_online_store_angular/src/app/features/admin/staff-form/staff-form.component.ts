import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { StaffService } from '../../../core/services/staff.service';
import { Role, StaffResponse } from '../../../core/models/staff.model';
import { ApiError } from '../../../core/models/common.model';

const ROLES: Role[] = ['ADMIN', 'STORE_MANAGER', 'SUPPORT_AGENT'];

@Component({
  selector: 'app-admin-staff-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './staff-form.component.html',
  styleUrl: './staff-form.component.css'
})
export class AdminStaffFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private staffService = inject(StaffService);

  readonly roles = ROLES;
  readonly loading = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly staffId = signal<number | null>(null);
  readonly existingStaff = signal<StaffResponse | null>(null);

  // Create-only fields
  readonly createForm = this.fb.group({
    username: ['', [Validators.required, Validators.maxLength(50)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    role: ['STORE_MANAGER' as Role, [Validators.required]]
  });

  // Edit-only fields - matches StaffUpdateRequest exactly (role + active only)
  readonly editForm = this.fb.group({
    role: ['STORE_MANAGER' as Role, [Validators.required]],
    active: [true, [Validators.required]]
  });

  get isEditMode(): boolean {
    return this.staffId() !== null;
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.staffId.set(id);
      this.staffService.getById(id).subscribe(s => {
        this.existingStaff.set(s);
        this.editForm.patchValue({ role: s.role, active: s.active });
      });
    }
  }

  onSubmit(): void {
    if (this.isEditMode) {
      this.submitEdit();
    } else {
      this.submitCreate();
    }
  }

  private submitCreate(): void {
    if (this.createForm.invalid) {
      this.createForm.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.errorMessage.set(null);
    const value = this.createForm.getRawValue();

    this.staffService.create({
      username: value.username!,
      email: value.email!,
      password: value.password!,
      role: value.role!
    }).subscribe({
      next: () => this.router.navigate(['/admin/staff']),
      error: (err: { error: ApiError }) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not create staff account.');
      }
    });
  }

  private submitEdit(): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.errorMessage.set(null);
    const value = this.editForm.getRawValue();

    this.staffService.update(this.staffId()!, { role: value.role!, active: value.active! }).subscribe({
      next: () => this.router.navigate(['/admin/staff']),
      error: (err: { error: ApiError }) => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not update staff account.');
      }
    });
  }
}
