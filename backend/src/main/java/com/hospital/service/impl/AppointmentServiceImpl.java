package com.hospital.service.impl;

import com.hospital.exception.ResourceNotFoundException;
import com.hospital.model.Appointment;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.payload.AppointmentRequest;
import com.hospital.payload.AppointmentResponse;
import com.hospital.payload.AvailabilityResponse;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    private static final int APPOINTMENT_DURATION_MINUTES = 30;

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest appointmentRequest) {
        Doctor doctor = doctorRepository.findById(appointmentRequest.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", appointmentRequest.getDoctorId()));

        Patient patient = patientRepository.findById(appointmentRequest.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", appointmentRequest.getPatientId()));

        LocalDateTime startTime = appointmentRequest.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);

        // Check for conflicts
        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                doctor.getId(), startTime, endTime);

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("The selected time slot is not available");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStartTime(startTime);
        appointment.setEndTime(endTime);
        appointment.setNotes(appointmentRequest.getNotes());

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return mapToAppointmentResponse(savedAppointment);
    }

    @Override
    public List<AvailabilityResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        LocalDateTime dayStart = date.atTime(LocalTime.MIN);
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        List<Appointment> existingAppointments = appointmentRepository
                .findByDoctorAndDateRange(doctorId, dayStart, dayEnd);

        List<AvailabilityResponse> availableSlots = new ArrayList<>();
        
        // Doctor's working hours (could be stored in DB)
        LocalTime workStart = LocalTime.of(9, 0);
        LocalTime workEnd = LocalTime.of(17, 0);
        
        LocalDateTime currentSlot = date.atTime(workStart);
        
        while (currentSlot.plusMinutes(APPOINTMENT_DURATION_MINUTES).toLocalTime().isBefore(workEnd)) {
            LocalDateTime slotEnd = currentSlot.plusMinutes(APPOINTMENT_DURATION_MINUTES);
            
            boolean isAvailable = existingAppointments.stream()
                    .noneMatch(appt -> isTimeOverlap(appt.getStartTime(), appt.getEndTime(), currentSlot, slotEnd));
            
            if (isAvailable) {
                availableSlots.add(new AvailabilityResponse(
                        doctorId,
                        doctor.getUser().getName(),
                        currentSlot,
                        slotEnd
                ));
            }
            
            currentSlot = currentSlot.plusMinutes(APPOINTMENT_DURATION_MINUTES);
        }
        
        return availableSlots;
    }

    private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, 
                                LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private AppointmentResponse mapToAppointmentResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getDoctor().getId(),
                appointment.getDoctor().getUser().getName(),
                appointment.getPatient().getId(),
                appointment.getPatient().getUser().getName(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus(),
                appointment.getNotes()
        );
    }
}