import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ActivityLogResponse } from '../models/activity-log.model';
import { PageResponse } from '../models/common.model';

@Injectable({ providedIn: 'root' })
export class ActivityLogService {
  private http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/activity-logs`;

  list(page = 0, size = 30): Observable<PageResponse<ActivityLogResponse>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PageResponse<ActivityLogResponse>>(this.baseUrl, { params });
  }
}
