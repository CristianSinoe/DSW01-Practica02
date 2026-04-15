import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../auth/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const session = authService.getSession();

  if (session && !req.headers.has('Authorization')) {
    req = req.clone({
      setHeaders: {
        Authorization: session.authHeader
      }
    });
  }

  return next(req);
};
