import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import {
  AbstractControl,
  ReactiveFormsModule,
  ValidationErrors,
  ValidatorFn,
  Validators,
  FormBuilder
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { UserRole } from '../../core/auth/auth.types';
import { Empleado } from './empleadas.models';
import { EmpleadasService } from './empleadas.service';

@Component({
  selector: 'app-empleadas-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './empleadas-form.component.html',
  styleUrl: './empleadas-form.component.css'
})
export class EmpleadasFormComponent implements OnInit {
  private formBuilder = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private empleadasService = inject(EmpleadasService);
  private empleadoId: string | null = null;

  isEditMode = false;
  isSubmitting = false;
  isLoading = false;
  errorMessage = '';

  roleOptions: UserRole[] = ['ADMIN', 'EMPLEADO'];

  form = this.formBuilder.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(150)]],
    telefono: ['', [Validators.required, Validators.maxLength(100)]],
    direccion: ['', [Validators.required, Validators.maxLength(100)]],
    password: ['', [Validators.maxLength(100), this.optionalMinLength(8)]],
    role: ['EMPLEADO' as UserRole, [Validators.required]]
  });

  ngOnInit(): void {
    this.empleadoId = this.route.snapshot.paramMap.get('id');
    this.isEditMode = !!this.empleadoId;
    this.configurePasswordValidators();

    if (this.empleadoId) {
      this.load(this.empleadoId);
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.errorMessage = '';
    this.isSubmitting = true;

    const formValue = this.form.getRawValue();

    const payload = {
      nombre: formValue.nombre,
      email: formValue.email,
      telefono: formValue.telefono,
      direccion: formValue.direccion,
      password: formValue.password,
      role: formValue.role,
      departamentoId: null
    };

    const request$ = this.isEditMode && this.empleadoId
      ? this.empleadasService.update(this.empleadoId, payload)
      : this.empleadasService.create(payload);

    request$.subscribe({
      next: (empleada) => {
        this.isSubmitting = false;
        this.router.navigate(['/empleadas', empleada.id]);
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = this.resolveErrorMessage(err, 'No se pudo guardar el empleado.');
      }
    });
  }

  private load(id: string): void {
    this.isLoading = true;
    this.empleadasService.getById(id).subscribe({
      next: (empleada: Empleado) => {
        this.form.patchValue({
          nombre: empleada.nombre,
          email: empleada.email,
          telefono: empleada.telefono,
          direccion: empleada.direccion,
          role: empleada.role,
          password: ''
        });
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = this.resolveErrorMessage(err, 'No se pudo cargar el empleado.');
      }
    });
  }

  private configurePasswordValidators(): void {
    const passwordControl = this.form.controls.password;
    const validators = [Validators.maxLength(100), this.optionalMinLength(8)];
    if (!this.isEditMode) {
      validators.unshift(Validators.required);
    }
    passwordControl.setValidators(validators);
    passwordControl.updateValueAndValidity();
  }

  private optionalMinLength(min: number): ValidatorFn {
    return (control: AbstractControl<string>): ValidationErrors | null => {
      const value = (control.value ?? '').trim();
      if (!value) {
        return null;
      }
      return value.length < min
        ? { minlength: { requiredLength: min, actualLength: value.length } }
        : null;
    };
  }

  private resolveErrorMessage(error: any, fallback: string): string {
    if (error?.status === 409) {
      return 'El email ya existe. Usa otro email.';
    }
    if (error?.status === 400) {
      return 'Revisa los datos del formulario e intenta de nuevo.';
    }
    if (error?.status === 404) {
      return 'El empleado ya no existe.';
    }
    if (error?.status === 403) {
      return 'No tienes permisos para esta accion.';
    }
    if (error?.status === 401) {
      return 'Tu sesion expiro. Inicia sesion nuevamente.';
    }
    return fallback;
  }
}
