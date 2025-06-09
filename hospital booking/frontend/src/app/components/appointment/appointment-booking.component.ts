import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AppointmentService } from '../../services/appointment.service';
import { DoctorService } from '../../services/doctor.service';
import { AuthService } from '../../services/auth.service';
import { Doctor } from '../../models/doctor.model';
import { Availability } from '../../models/availability.model';
import { AppointmentRequest } from '../../models/appointment-request.model';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-appointment-booking',
  templateUrl: './appointment-booking.component.html',
  styleUrls: ['./appointment-booking.component.scss']
})
export class AppointmentBookingComponent implements OnInit {
  bookingForm: FormGroup;
  doctors: Doctor[] = [];
  specializations: string[] = [];
  availableSlots: Availability[] = [];
  selectedDoctor: Doctor | null = null;
  selectedDate: Date | null = null;
  isLoading = false;

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService,
    private doctorService: DoctorService,
    private authService: AuthService,
    private toastr: ToastrService
  ) {
    this.bookingForm = this.fb.group({
      specialization: ['', Validators.required],
      doctorId: ['', Validators.required],
      date: ['', Validators.required],
      slot: ['', Validators.required],
      notes: ['']
    });
  }

  ngOnInit(): void {
    this.loadDoctors();
  }

  loadDoctors(): void {
    this.doctorService.getDoctors().subscribe(doctors => {
      this.doctors = doctors;
      this.specializations = [...new Set(doctors.map(d => d.specialization))];
    });
  }

  onSpecializationChange(): void {
    const specialization = this.bookingForm.get('specialization')?.value;
    this.bookingForm.get('doctorId')?.reset();
    this.selectedDoctor = null;
    this.availableSlots = [];
    
    if (specialization) {
      this.selectedDoctor = this.doctors.find(d => d.specialization === specialization) || null;
      if (this.selectedDoctor) {
        this.bookingForm.get('doctorId')?.setValue(this.selectedDoctor.id);
      }
    }
  }

  onDateChange(): void {
    const doctorId = this.bookingForm.get('doctorId')?.value;
    const date = this.bookingForm.get('date')?.value;
    
    if (doctorId && date) {
      this.isLoading = true;
      this.appointmentService.getDoctorAvailability(doctorId, new Date(date))
        .subscribe({
          next: (slots) => {
            this.availableSlots = slots;
            this.isLoading = false;
          },
          error: (err) => {
            this.toastr.error('Failed to load available slots');
            this.isLoading = false;
          }
        });
    }
  }

  bookAppointment(): void {
    if (this.bookingForm.invalid) {
      this.toastr.warning('Please fill all required fields');
      return;
    }

    const formValue = this.bookingForm.value;
    const selectedSlot = this.availableSlots.find(slot => 
      slot.startTime === formValue.slot.startTime && 
      slot.endTime === formValue.slot.endTime
    );

    if (!selectedSlot) {
      this.toastr.error('Selected slot is no longer available');
      return;
    }

    const currentUser = this.authService.currentUserValue;
    if (!currentUser || !currentUser.patientId) {
      this.toastr.error('Patient information not found');
      return;
    }

    const appointmentRequest: AppointmentRequest = {
      doctorId: formValue.doctorId,
      patientId: currentUser.patientId,
      startTime: selectedSlot.startTime,
      notes: formValue.notes
    };

    this.isLoading = true;
    this.appointmentService.createAppointment(appointmentRequest).subscribe({
      next: (appointment) => {
        this.toastr.success('Appointment booked successfully');
        this.bookingForm.reset();
        this.availableSlots = [];
        this.isLoading = false;
      },
      error: (err) => {
        this.toastr.error('Failed to book appointment');
        this.isLoading = false;
      }
    });
  }
}