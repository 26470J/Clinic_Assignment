package kigali.clinc.clinicmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.service.AppointmentService;

@RestController
@RequestMapping(value = "/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping(value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveAppointment(
            @RequestBody Appointment appointment,
            @RequestParam String patientFirstName,
            @RequestParam String patientLastName,
            @RequestParam String doctorFirstName,
            @RequestParam String doctorLastName) {
        String msg = appointmentService.saveAppointment(appointment,
                patientFirstName, patientLastName, doctorFirstName, doctorLastName);
        if (msg.equalsIgnoreCase("Appointment is saved Successfully"))
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        if (msg.equalsIgnoreCase("Appointment date cannot be in the past"))
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        if (msg.equalsIgnoreCase("Doctor is already booked on that date"))
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        if (msg.startsWith("Patient ") || msg.startsWith("Doctor "))
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateStatus(@PathVariable UUID id,
                                          @RequestParam AppointmentStatus status) {
        String msg = appointmentService.updateStatus(id, status);
        if (msg.equalsIgnoreCase("The appointment with that id does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.startsWith("Cannot change status from")) {
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @PatchMapping(value = "/{id}/reschedule", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reschedule(@PathVariable UUID id,
                                        @RequestParam String newDate) {
        String msg = appointmentService.rescheduleAppointment(
                id, java.sql.Date.valueOf(LocalDate.parse(newDate)));
        if (msg.equalsIgnoreCase("The appointment with that id does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.startsWith("A COMPLETED appointment")
                || msg.startsWith("A CANCELLED appointment")
                || msg.equalsIgnoreCase("Appointment date cannot be in the past")) {
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        }
        if (msg.equalsIgnoreCase("Doctor is already booked on that date")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteAppointment(@PathVariable UUID id) {
        String msg = appointmentService.deleteAppointment(id);
        if (msg.equalsIgnoreCase("The appointment with that id does not exist"))
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.equalsIgnoreCase("That appointment has prescriptions, delete them first"))
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> all() {
        return new ResponseEntity<>(appointmentService.getAllAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> byStatus(@RequestParam AppointmentStatus status) {
        return new ResponseEntity<>(appointmentService.getAppointmentsByStatus(status), HttpStatus.OK);
    }

    @GetMapping(value = "/between", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> between(@RequestParam String start,
                                                     @RequestParam String end) {
        return new ResponseEntity<>(
                appointmentService.getAppointmentsBetween(
                        java.sql.Date.valueOf(LocalDate.parse(start)),
                        java.sql.Date.valueOf(LocalDate.parse(end))),
                HttpStatus.OK);
    }

    @GetMapping(value = "/stats/by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> statsByStatus() {
        return new ResponseEntity<>(appointmentService.getAppointmentStatsByStatus(), HttpStatus.OK);
    }

    @PatchMapping(value = "/cancel-day", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelDay(@RequestParam UUID doctorId,
                                       @RequestParam String date) {
        return new ResponseEntity<>(
                appointmentService.cancelDoctorDay(
                        doctorId, java.sql.Date.valueOf(LocalDate.parse(date))),
                HttpStatus.OK);
    }

    @GetMapping(value = "/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Appointment>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "appointmentDate,desc") String sort) {
        String[] parts = sort.split(",");
        Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, parts[0]));
        return new ResponseEntity<>(appointmentService.getPagedAppointments(pageable), HttpStatus.OK);
    }

    @DeleteMapping(value = "/cancelled-before", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelledBefore(@RequestParam String date) {
        return new ResponseEntity<>(
                appointmentService.deleteOldCancellations(
                        java.sql.Date.valueOf(LocalDate.parse(date))),
                HttpStatus.OK);
    }
}
