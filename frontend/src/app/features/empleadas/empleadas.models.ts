import { UserRole } from '../../core/auth/auth.types';

export interface Empleado {
  id: string;
  nombre: string;
  email: string;
  telefono: string;
  direccion: string;
  role: UserRole;
  departamentoId: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface EmpleadoPage {
  content: Empleado[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface EmpleadoCreatePayload {
  nombre: string;
  email: string;
  telefono: string;
  direccion: string;
  password: string;
  role: UserRole;
  departamentoId?: string | null;
}

export interface EmpleadoUpdatePayload {
  nombre: string;
  email: string;
  telefono: string;
  direccion: string;
  password?: string | null;
  role: UserRole;
  departamentoId?: string | null;
}
