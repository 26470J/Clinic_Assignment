package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.PatientRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;
    private final PatientRepository patientRepo;
    private final PrescriptionRepository prescriptionRepo;

    public AppointmentService(AppointmentRepository appointmentRepo,
                              DoctorRepository doctorRepo,
                              PatientRepository patientRepo,
                              PrescriptionRepository prescriptionRepo) {
        this.appointmentRepo = appointmentRepo;
        this.doctorRepo = doctorRepo;
        this.patientRepo = patientRepo;
        this.prescriptionRepo = prescriptionRepo;
    }

    public String saveAppointment(Appointment appointment,
                                  String patientFirstName, String patientLastName,
                                  String doctorFirstName, String doctorLastName) {
        if (appointment.getAppointmentDate() == null) {
            return "Appointment date cannot be in the past";
        }
        java.time.LocalDate apptDate = appointment.getAppointmentDate().toLocalDate();
        if (apptDate.isBefore(java.time.LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        Optional<Patient> patient = patientRepo.findByFirstNameAndLastName(patientFirstName, patientLastName);
        if (patient.isEmpty()) return "Patient " + patientFirstName + " " + patientLastName + " not found";

        Optional<Doctor> doctor = doctorRepo.findByFirstNameAndLastName(doctorFirstName, doctorLastName);
        if (doctor.isEmpty()) return "Doctor " + doctorFirstName + " " + doctorLastName + " not found";

        Boolean alreadyBooked = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                doctor.get().getId(),
                appointment.getAppointmentDate(),
                AppointmentStatus.CANCELLED);
        if (Boolean.TRUE.equals(alreadyBooked)) {
            return "Doctor is already booked on that date";
        }

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());
        appointmentRepo.save(appointment);
        return "Appointment is saved Successfully";
    }

    public String updateStatus(UUID id, AppointmentStatus newStatus) {
        Optional<Appointment> opt = appointmentRepo.findById(id);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = opt.get();
        AppointmentStatus current = appointment.getStatus();

        boolean allowed;
        if (current == AppointmentStatus.SCHEDULED) {
            allowed = (newStatus == AppointmentStatus.CONFIRMED
                    || newStatus == AppointmentStatus.CANCELLED);
        } else if (current == AppointmentStatus.CONFIRMED) {
            allowed = (newStatus == AppointmentStatus.COMPLETED
                    || newStatus == AppointmentStatus.CANCELLED);
        } else {
            allowed = false;
        }

        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        appointment.setStatus(newStatus);
        appointmentRepo.save(appointment);
        return "Appointment status updated successfully";
    }

    public String rescheduleAppointment(UUID id, java.sql.Date newDate) {
        Optional<Appointment> opt = appointmentRepo.findById(id);
        if (opt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = opt.get();

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return "A COMPLETED appointment cannot be rescheduled";
        }
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return "A CANCELLED appointment cannot be rescheduled";
        }

        if (newDate == null || newDate.toLocalDate().isBefore(java.time.LocalDate.now())) {
            return "Appointment date cannot be in the past";
        }

        Boolean alreadyBooked = appointmentRepo.existsByDoctorIdAndAppointmentDateAndStatusNot(
                appointment.getDoctor().getId(),
                newDate,
                AppointmentStatus.CANCELLED);
        if (Boolean.TRUE.equals(alreadyBooked)) {
            return "Doctor is already booked on that date";
        }

        appointment.setAppointmentDate(newDate);
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepo.save(appointment);
        return "Appointment rescheduled successfully";
    }

    public List<Appointment> getDoctorAgenda(UUID doctorId, java.sql.Date date) {
        return appointmentRepo
                .findByDoctorIdAndAppointmentDateAndStatusNotOrderByAppointmentDateAsc(
                        doctorId, date, AppointmentStatus.CANCELLED);
    }

    public List<Appointment> getUpcomingForPatient(String ssNumber) {
        return appointmentRepo
                .findByPatientSsNumberAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAsc(
                        ssNumber,
                        java.sql.Date.valueOf(java.time.LocalDate.now()),
                        java.util.List.of(AppointmentStatus.SCHEDULED, AppointmentStatus.CONFIRMED));
    }

    public String deleteAppointment(UUID id) {
        if (!appointmentRepo.existsById(id)) {
            return "The appointment with that id does not exist";
        }
        if (Boolean.TRUE.equals(prescriptionRepo.existsByAppointmentId(id))) {
            return "That appointment has prescriptions, delete them first";
        }
        appointmentRepo.deleteById(id);
        return "Appointment deleted successfully";
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }

    public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepo.findByStatusOrderByAppointmentDateAsc(status);
    }

    public List<Appointment> getAppointmentsBetween(java.sql.Date start, java.sql.Date end) {
        return appointmentRepo.findByAppointmentDateBetweenOrderByAppointmentDateAsc(start, end);
    }

    public List<Object[]> getAppointmentStatsByStatus() {
        return appointmentRepo.countAppointmentsByStatus();
    }

    @Transactional
    public String cancelDoctorDay(UUID doctorId, java.sql.Date date) {
        int count = appointmentRepo.cancelAppointmentsForDoctorOnDate(
                doctorId, date, AppointmentStatus.CANCELLED, AppointmentStatus.COMPLETED);
        return count + " appointments cancelled";
    }

    public Page<Appointment> getPagedAppointments(Pageable pageable) {
        return appointmentRepo.findAll(pageable);
    }

    @Transactional
    public String deleteOldCancellations(java.sql.Date date) {
        int count = appointmentRepo.deleteOldCancellations(AppointmentStatus.CANCELLED, date);
        return count + " appointments deleted";
    }

    public List<Appointment> getPendingAppointmentsForDoctor(UUID doctorId) {
        return appointmentRepo.findByDoctorIdAndStatusOrderByAppointmentDateAsc(
                doctorId, AppointmentStatus.SCHEDULED);
    }

    public Page<Appointment> getDoctorAppointmentsPage(UUID doctorId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page, size, org.springframework.data.domain.Sort.by("appointmentDate").ascending());
        return appointmentRepo.findByDoctorId(doctorId, pageable);
    }
}
