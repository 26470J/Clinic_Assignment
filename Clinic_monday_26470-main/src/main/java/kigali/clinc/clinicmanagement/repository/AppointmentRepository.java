package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByDoctorId(UUID doctorId);
    List<Appointment> findByPatientId(UUID patientId);
    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByDoctorIdAndStatusOrderByAppointmentDateAsc(
            UUID doctorId, AppointmentStatus status);

    Page<Appointment> findByDoctorId(UUID doctorId, Pageable pageable);

    List<Appointment> findByStatusOrderByAppointmentDateAsc(AppointmentStatus status);

    List<Appointment> findByAppointmentDateBetweenOrderByAppointmentDateAsc(
            java.sql.Date start, java.sql.Date end);

    Boolean existsByDoctorIdAndAppointmentDateAndStatusNot(
            UUID doctorId, java.sql.Date appointmentDate, AppointmentStatus status);

    List<Appointment> findByDoctorIdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
            UUID doctorId, java.sql.Date appointmentDate, AppointmentStatus status);

    List<Appointment> findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
            String ssNumber,
            java.sql.Date date,
            java.util.List<AppointmentStatus> statuses);

    long countByAppointmentDate(java.sql.Date appointmentDate);

    long countByStatus(AppointmentStatus status);

    @Query("SELECT a.status, COUNT(a) FROM Appointment a GROUP BY a.status")
    List<Object[]> countAppointmentsByStatus();

    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :cancelled " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentDate = :date " +
           "AND a.status <> :completed")
    int cancelAppointmentsForDoctorOnDate(
            @Param("doctorId") UUID doctorId,
            @Param("date") java.sql.Date date,
            @Param("cancelled") AppointmentStatus cancelled,
            @Param("completed") AppointmentStatus completed);

    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a " +
           "WHERE a.status = :status AND a.appointmentDate < :date")
    int deleteOldCancellations(@Param("status") AppointmentStatus status,
                               @Param("date") java.sql.Date date);
}
