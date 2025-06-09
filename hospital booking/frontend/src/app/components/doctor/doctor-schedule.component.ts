import { Component, OnInit } from '@angular/core';
import { AppointmentService } from '../../services/appointment.service';
import { AuthService } from '../../services/auth.service';
import { Appointment } from '../../models/appointment.model';
import { ToastrService } from 'ngx-toastr';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-doctor-schedule',
  templateUrl: './doctor-schedule.component.html',
  styleUrls: ['./doctor-schedule.component.scss'],
  providers: [DatePipe]
})
export class DoctorScheduleComponent implements OnInit {
  appointments: Appointment[] = [];
  isLoading = false;
  selectedDate: Date = new Date();

  constructor(
    private appointmentService: AppointmentService,
    private authService: AuthService,
    private toastr: ToastrService,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.loadAppointments();
  }

  loadAppointments(): void {
    const currentUser = this.authService.currentUser; // or use a getter or observable as per your AuthService implementation
    if (!currentUser || !currentUser.doctorId) {
      this.toastr.error('Doctor information not found');
      return;
    }

    this.isLoading = true;
    this.appointmentService.getDoctorAppointments(currentUser.doctorId)
      .subscribe({
        next: (appointments) => {
          this.appointments = appointments;
          this.isLoading = false;
        },
        error: (err) => {
          this.toastr.error('Failed to load appointments');
          this.isLoading = false;
        }
      });
  }

  onDateChange(date: Date): void {
    this.selectedDate = date;
    // Implement filtering by date if needed
  }

  formatAppointmentTime(start: string, end: string): string {
    return `${this.datePipe.transform(start, 'shortTime')} - ${this.datePipe.transform(end, 'shortTime')}`;
  }

  getAppointmentsForSelectedDate(): Appointment[] {
    if (!this.selectedDate) return this.appointments;
    
    return this.appointments.filter(appt => {
      const apptDate = new Date(appt.startTime);
      return apptDate.getDate() === this.selectedDate.getDate() &&
             apptDate.getMonth() === this.selectedDate.getMonth() &&
             apptDate.getFullYear() === this.selectedDate.getFullYear();
    });
  }
}