import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AddressService } from '../../../core/services/address.service';
import { AddressResponse } from '../../../core/models/address.model';
import { ApiError } from '../../../core/models/common.model';

@Component({
  selector: 'app-addresses',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './addresses.component.html',
  styleUrl: './addresses.component.css'
})
export class AddressesComponent implements OnInit {
  private fb = inject(FormBuilder);
  private addressService = inject(AddressService);

  readonly addresses = signal<AddressResponse[]>([]);
  readonly loading = signal(true);
  readonly saving = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly editingId = signal<number | null>(null);
  readonly showForm = signal(false);

  readonly form = this.fb.group({
    label: [''],
    line1: ['', [Validators.required]],
    line2: [''],
    city: ['', [Validators.required]],
    state: [''],
    postalCode: [''],
    country: ['', [Validators.required]],
    isDefault: [false]
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.addressService.list().subscribe(addresses => {
      this.addresses.set(addresses);
      this.loading.set(false);
    });
  }

  startAdd(): void {
    this.editingId.set(null);
    this.form.reset({ isDefault: false });
    this.showForm.set(true);
  }

  startEdit(address: AddressResponse): void {
    this.editingId.set(address.id);
    this.form.reset(address);
    this.showForm.set(true);
  }

  cancel(): void {
    this.showForm.set(false);
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);
    const value = this.form.getRawValue();
    const request = {
      label: value.label || undefined,
      line1: value.line1!,
      line2: value.line2 || undefined,
      city: value.city!,
      state: value.state || undefined,
      postalCode: value.postalCode || undefined,
      country: value.country!,
      isDefault: value.isDefault!
    };

    const editingId = this.editingId();
    const save$ = editingId ? this.addressService.update(editingId, request) : this.addressService.add(request);

    save$.subscribe({
      next: () => {
        this.saving.set(false);
        this.showForm.set(false);
        this.load();
      },
      error: (err: { error: ApiError }) => {
        this.saving.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not save address.');
      }
    });
  }

  delete(address: AddressResponse): void {
    if (!confirm('Delete this address?')) return;
    this.addressService.delete(address.id).subscribe(() => this.load());
  }
}
