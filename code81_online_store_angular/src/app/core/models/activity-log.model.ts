export interface ActivityLogResponse {
  id: number;
  staffUserId: number;
  staffUsername: string;
  action: string;
  entityType: string;
  entityId?: number;
  details?: string;
  timestamp: string;
}
