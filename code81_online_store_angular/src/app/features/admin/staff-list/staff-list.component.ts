import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { StaffService } from '../../../core/services/staff.service';
import { StaffResponse } from '../../../core/models/staff.model';

@Component({
  selector: 'app-admin-staff-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './staff-list.component.html',
  styleUrl: './staff-list.component.css'
})
export class AdminStaffListComponent implements OnInit {
  private staffService = inject(StaffService);

  readonly staff = signal<StaffResponse[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.staffService.list(0, 50).subscribe(res => {
      this.staff.set(res.content);
      this.loading.set(false);
    });
  }
}
