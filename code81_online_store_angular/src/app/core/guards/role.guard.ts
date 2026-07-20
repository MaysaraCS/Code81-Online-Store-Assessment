import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Factory, not a plain guard, so routes can say which roles are allowed:
 *   canActivate: [roleGuard(['ADMIN', 'STORE_MANAGER'])]
 */
export function roleGuard(allowedRoles: string[]): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (authService.isAuthenticated() && allowedRoles.includes(authService.role() ?? '')) {
      return true;
    }
    router.navigate(['/']);
    return false;
  };
}
