import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Empleado,
  EmpleadoCreatePayload,
  EmpleadoPage,
  EmpleadoUpdatePayload
} from './empleadas.models';

@Injectable({ providedIn: 'root' })
export class EmpleadasService {
  private baseUrl = `${environment.apiBaseUrl}/empleados`;

  constructor(private http: HttpClient) {}

  list(page = 0, size = 10, sort = 'email,asc'): Observable<EmpleadoPage> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);
    return this.http.get<EmpleadoPage>(this.baseUrl, { params });
  }

  getById(id: string): Observable<Empleado> {
    return this.http.get<Empleado>(`${this.baseUrl}/${id}`);
  }

  create(payload: EmpleadoCreatePayload): Observable<Empleado> {
    return this.http.post<Empleado>(this.baseUrl, payload);
  }

  update(id: string, payload: EmpleadoUpdatePayload): Observable<Empleado> {
    return this.http.put<Empleado>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
