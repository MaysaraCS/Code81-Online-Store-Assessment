import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const AUTH_FREE_PATHS = ['/auth/customers/login', '/auth/staff/login', '/auth/refresh'];

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isAuthEndpoint = AUTH_FREE_PATHS.some(path => req.url.includes(path));
  const token = authService.getAccessToken();

  const authorizedReq = token && !isAuthEndpoint
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authorizedReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Only attempt a refresh-and-retry for a real "your access token expired"
      // case - never for the auth endpoints themselves, or we'd loop forever.
      if (error.status === 401 && !isAuthEndpoint && authService.getRefreshToken()) {
        return authService.refresh().pipe(
          switchMap(() => {
            const retryReq = req.clone({
              setHeaders: { Authorization: `Bearer ${authService.getAccessToken()}` }
            });
            return next(retryReq);
          }),
          catchError(refreshError => {
            // Refresh token is also invalid/expired - nothing left to do but log out.
            authService.logout();
            router.navigate(['/login']);
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
};
