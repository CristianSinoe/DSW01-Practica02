export type UserRole = 'ADMIN' | 'EMPLEADO';

export interface AuthSession {
  id: string;
  nombre: string;
  email: string;
  role: UserRole;
  authHeader: string;
}
