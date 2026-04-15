import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { UserRole } from '../auth/auth.types';

export const roleGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const roles = route.data?.['roles'] as UserRole[] | undefined;

  if (!roles || roles.some((role) => authService.hasRole(role))) {
    return true;
  }

  return router.createUrlTree(['/empleadas']);
};
