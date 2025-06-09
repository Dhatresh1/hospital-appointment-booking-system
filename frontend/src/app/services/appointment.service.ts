import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Appointment, Availability, Doctor, AppointmentRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AppointmentService {
  private apiUrl = `${environment.apiUrl}/appointments`;

  constructor(private http: HttpClient) { }

  getAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(this.apiUrl);
  }

  createAppointment(appointment: AppointmentRequest): Observable<Appointment> {
    return this.http.post<Appointment>(this.apiUrl, appointment);
  }

  getDoctorAvailability(doctorId: number, date: Date): Observable<Availability[]> {
    const dateStr = date.toISOString().split('T')[0];
    return this.http.get<Availability[]>(`${this.apiUrl}/doctor/${doctorId}/availability?date=${dateStr}`);
  }

  getPatientAppointments(patientId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/patient/${patientId}`);
  }

  getDoctorAppointments(doctorId: number): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.apiUrl}/doctor/${doctorId}`);
  }

  cancelAppointment(appointmentId: number): Observable<Appointment> {
    return this.http.put<Appointment>(`${this.apiUrl}/${appointmentId}/cancel`, {});
  }
}