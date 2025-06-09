package com.hospital.controller;

import com.hospital.payload.AppointmentRequest;
import com.hospital.payload.AppointmentResponse;
import com.hospital.payload.AvailabilityResponse;
import com.hospital.security.CurrentUser;
import com.hospital.security.UserPrincipal;
import com.hospital.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest appointmentRequest,
            @CurrentUser UserPrincipal currentUser) {
        
        if (!currentUser.getId().equals(appointmentRequest.getPatientId())) {
            return ResponseEntity.badRequest().build();
        }
        
        AppointmentResponse response = appointmentService.createAppointment(appointmentRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/{doctorId}/availability")
    public ResponseEntity<List<AvailabilityResponse>> getDoctorAvailability(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        List<AvailabilityResponse> availability = appointmentService.getAvailableSlots(doctorId, date);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getPatientAppointments(
            @PathVariable Long patientId,
            @CurrentUser UserPrincipal currentUser) {
        
        if (currentUser.getRoles().stream().noneMatch(role -> role.getName().name().equals("ROLE_ADMIN")) {
            if (!currentUser.getId().equals(patientId)) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        List<AppointmentResponse> appointments = appointmentService.getPatientAppointments(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getDoctorAppointments(
            @PathVariable Long doctorId,
            @CurrentUser UserPrincipal currentUser) {
        
        if (currentUser.getRoles().stream().noneMatch(role -> role.getName().name().equals("ROLE_ADMIN")) {
            if (!currentUser.getId().equals(doctorId)) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        List<AppointmentResponse> appointments = appointmentService.getDoctorAppointments(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{appointmentId}/cancel")
    @PreAuthorize("hasRole('PATIENT') or hasRole('ADMIN')")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @PathVariable Long appointmentId,
            @CurrentUser UserPrincipal currentUser) {
        
        AppointmentResponse response = appointmentService.cancelAppointment(appointmentId, currentUser);
        return ResponseEntity.ok(response);
    }
}