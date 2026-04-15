import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthSession, UserRole } from './auth.types';
import { clearSession, loadSession, saveSession } from './auth.storage';

interface LoginResponse {
  id: string;
  nombre: string;
  email: string;
  role: UserRole;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private sessionSubject = new BehaviorSubject<AuthSession | null>(loadSession());
  readonly session$ = this.sessionSubject.asObservable();

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<AuthSession> {
    const normalizedEmail = email.trim();
    const authHeader = this.buildAuthHeader(normalizedEmail, password);
    const headers = new HttpHeaders({ Authorization: authHeader });

    return this.http
      .get<LoginResponse>(`${environment.apiBaseUrl}/auth/me`, { headers })
      .pipe(
        map((response) => ({ ...response, authHeader })),
        tap((session) => {
          saveSession(session);
          this.sessionSubject.next(session);
        })
      );
  }

  logout(): void {
    clearSession();
    this.sessionSubject.next(null);
  }

  getSession(): AuthSession | null {
    return this.sessionSubject.value;
  }

  isAuthenticated(): boolean {
    return this.sessionSubject.value !== null;
  }

  hasRole(role: UserRole): boolean {
    return this.sessionSubject.value?.role === role;
  }

  private buildAuthHeader(email: string, password: string): string {
    return `Basic ${btoa(`${email}:${password}`)}`;
  }
}
