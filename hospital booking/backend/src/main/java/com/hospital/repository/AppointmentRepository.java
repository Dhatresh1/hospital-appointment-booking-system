package com.hospital.repository;

import com.hospital.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND " +
           "((a.startTime BETWEEN :start AND :end) OR " +
           "(a.endTime BETWEEN :start AND :end) OR " +
           "(a.startTime <= :start AND a.endTime >= :end))")
    List<Appointment> findConflictingAppointments(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.startTime >= :start AND a.endTime <= :end")
    List<Appointment> findByDoctorAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
