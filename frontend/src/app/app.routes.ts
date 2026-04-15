import { Routes } from '@angular/router';

export const routes: Routes = [
	{
		path: '',
		redirectTo: 'login',
		pathMatch: 'full'
	},
	{
		path: 'empleadas',
		loadChildren: () =>
			import('./features/empleadas/empleadas.routes').then((m) => m.empleadasRoutes)
	},
	{
		path: 'login',
		loadComponent: () =>
			import('./features/auth/login.component').then((m) => m.LoginComponent)
	},
	{
		path: '**',
		redirectTo: 'login'
	}
];
