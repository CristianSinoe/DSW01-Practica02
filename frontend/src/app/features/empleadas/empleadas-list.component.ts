import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Empleado } from './empleadas.models';
import { EmpleadasService } from './empleadas.service';

@Component({
  selector: 'app-empleadas-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './empleadas-list.component.html',
  styleUrl: './empleadas-list.component.css'
})
export class EmpleadasListComponent implements OnInit {
  empleadas: Empleado[] = [];
  isLoading = false;
  errorMessage = '';
  isAdmin = false;

  constructor(
    private empleadasService: EmpleadasService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.hasRole('ADMIN');
    this.load();
  }

  load(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.empleadasService.list().subscribe({
      next: (page) => {
        this.empleadas = page.content;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = this.resolveErrorMessage(err, 'No se pudo cargar la lista.');
      }
    });
  }

  handleDelete(id: string): void {
    if (!confirm('Eliminar este empleado de forma definitiva?')) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.empleadasService.delete(id).subscribe({
      next: () => this.load(),
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = this.resolveErrorMessage(err, 'No se pudo eliminar el empleado.');
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private resolveErrorMessage(error: any, fallback: string): string {
    if (error?.status === 403) {
      return 'No tienes permisos para esta accion.';
    }
    if (error?.status === 401) {
      return 'Tu sesion expiro. Inicia sesion nuevamente.';
    }
    return fallback;
  }
}
