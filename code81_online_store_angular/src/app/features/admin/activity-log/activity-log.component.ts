import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivityLogService } from '../../../core/services/activity-log.service';
import { ActivityLogResponse } from '../../../core/models/activity-log.model';

@Component({
  selector: 'app-admin-activity-log',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './activity-log.component.html',
  styleUrl: './activity-log.component.css'
})
export class AdminActivityLogComponent implements OnInit {
  private activityLogService = inject(ActivityLogService);

  readonly logs = signal<ActivityLogResponse[]>([]);
  readonly loading = signal(true);

  ngOnInit(): void {
    this.activityLogService.list(0, 50).subscribe(res => {
      this.logs.set(res.content);
      this.loading.set(false);
    });
  }
}
