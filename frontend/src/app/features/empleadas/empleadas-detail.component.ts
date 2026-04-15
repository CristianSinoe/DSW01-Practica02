import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Empleado } from './empleadas.models';
import { EmpleadasService } from './empleadas.service';

@Component({
  selector: 'app-empleadas-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './empleadas-detail.component.html',
  styleUrl: './empleadas-detail.component.css'
})
export class EmpleadasDetailComponent implements OnInit {
  empleada: Empleado | null = null;
  isLoading = false;
  errorMessage = '';
  isAdmin = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private empleadasService: EmpleadasService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.authService.hasRole('ADMIN');
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.errorMessage = 'No se encontro el empleado solicitado.';
      return;
    }
    this.load(id);
  }

  load(id: string): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.empleadasService.getById(id).subscribe({
      next: (empleada) => {
        this.empleada = empleada;
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = this.resolveErrorMessage(err, 'No se pudo cargar el detalle.');
      }
    });
  }

  handleDelete(): void {
    if (!this.empleada) {
      return;
    }
    if (!confirm('Eliminar este empleado de forma definitiva?')) {
      return;
    }

    this.isLoading = true;
    this.empleadasService.delete(this.empleada.id).subscribe({
      next: () => this.router.navigate(['/empleadas']),
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = this.resolveErrorMessage(err, 'No se pudo eliminar el empleado.');
      }
    });
  }

  private resolveErrorMessage(error: any, fallback: string): string {
    if (error?.status === 404) {
      return 'El empleado ya no existe.';
    }
    if (error?.status === 403) {
      return 'No tienes permisos para ver esta informacion.';
    }
    if (error?.status === 401) {
      return 'Tu sesion expiro. Inicia sesion nuevamente.';
    }
    return fallback;
  }
}
