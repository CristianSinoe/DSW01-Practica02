import { Routes } from '@angular/router';
import { authGuard } from '../../core/guards/auth.guard';
import { roleGuard } from '../../core/guards/role.guard';

export const empleadasRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./empleadas-list.component').then((m) => m.EmpleadasListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'nueva',
    loadComponent: () =>
      import('./empleadas-form.component').then((m) => m.EmpleadasFormComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] }
  },
  {
    path: ':id',
    loadComponent: () =>
      import('./empleadas-detail.component').then((m) => m.EmpleadasDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: ':id/editar',
    loadComponent: () =>
      import('./empleadas-form.component').then((m) => m.EmpleadasFormComponent),
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] }
  }
];
