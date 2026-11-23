package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.DoctorService;

@RestController
@RequestMapping(value = "/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @PostMapping(value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveDoctor(@RequestBody Doctor doctor,
                                        @RequestParam int officeNumber) {
        String msg = doctorService.saveDoctor(doctor, officeNumber);
        if (msg.equalsIgnoreCase("Doctor is saved Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.CREATED);
        }
        if (msg.startsWith("Office with number")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDoctorById(@PathVariable UUID id) {
        Doctor doctor = doctorService.getDoctorById(id);
        return doctor == null
                ? new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateDoctor(@PathVariable UUID id, @RequestBody Doctor doctor) {
        String msg = doctorService.updateDoctor(id, doctor);
        return msg.equalsIgnoreCase("Doctor updated Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        String msg = doctorService.deleteDoctor(id);
        return msg.equalsIgnoreCase("Doctor deleted Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/{doctorId}/specializations/{specializationId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSpecialization(@PathVariable UUID doctorId,
                                               @PathVariable UUID specializationId) {
        String msg = doctorService.addSpecialization(doctorId, specializationId);
        if (msg.equals("Specialization added to the doctor")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equals("The doctor already has that specialization")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/{doctorId}/specializations/{specializationId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeSpecialization(@PathVariable UUID doctorId,
                                                  @PathVariable UUID specializationId) {
        String msg = doctorService.removeSpecialization(doctorId, specializationId);
        if (msg.equals("Specialization removed from the doctor")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        }
        if (msg.equals("The doctor does not have that specialization")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/{doctorId}/specializations",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> assignSpecializations(@PathVariable UUID doctorId,
                                                   @RequestBody List<UUID> specializationIds) {
        String msg = doctorService.assignSpecializations(doctorId, specializationIds);
        return msg.equalsIgnoreCase("Specializations assigned Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @PatchMapping(value = "/{doctorId}/office", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeOffice(@PathVariable UUID doctorId,
                                          @RequestParam int officeNumber) {
        String msg = doctorService.changeOffice(doctorId, officeNumber);
        if (msg.equalsIgnoreCase("The doctor with that id does not exist")
                || msg.equalsIgnoreCase("The office with that office number does not exist")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        }
        if (msg.equalsIgnoreCase("That office is taken by other doctor")) {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @GetMapping(value = "/search/firstname", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> searchByFirstName(@RequestParam String firstName) {
        return new ResponseEntity<>(doctorService.getDoctorsByFirstName(firstName), HttpStatus.OK);
    }

    @GetMapping(value = "/count/by-specialization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> countBySpecialization(@RequestParam String specialization) {
        return new ResponseEntity<>(
                doctorService.countDoctorsBySpecialization(specialization), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Appointment>> getPendingAppointments(@PathVariable UUID id) {
        return new ResponseEntity<>(
                appointmentService.getPendingAppointmentsForDoctor(id), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/page", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Appointment>> getPagedAppointments(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return new ResponseEntity<>(
                appointmentService.getDoctorAppointmentsPage(id, page, size), HttpStatus.OK);
    }

    @GetMapping(value = "/without-appointments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> withoutAppointments() {
        return new ResponseEntity<>(doctorService.getDoctorsWithoutAppointments(), HttpStatus.OK);
    }

    @GetMapping(value = "/by-specialization", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> bySpecialization(@RequestParam String name) {
        return new ResponseEntity<>(doctorService.getDoctorsBySpecialization(name), HttpStatus.OK);
    }

    @GetMapping(value = "/{doctorId}/agenda", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> agenda(@PathVariable UUID doctorId,
                                    @RequestParam String date) {
        if (doctorService.getDoctorById(doctorId) == null) {
            return new ResponseEntity<>("The doctor with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(
                appointmentService.getDoctorAgenda(
                        doctorId, java.sql.Date.valueOf(java.time.LocalDate.parse(date))),
                HttpStatus.OK);
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> search(@RequestParam String name) {
        return new ResponseEntity<>(doctorService.searchDoctorsByName(name), HttpStatus.OK);
    }

    @GetMapping(value = "/workload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> workload(@RequestParam String start,
                                                   @RequestParam String end) {
        return new ResponseEntity<>(
                doctorService.getDoctorWorkload(
                        java.sql.Date.valueOf(java.time.LocalDate.parse(start)),
                        java.sql.Date.valueOf(java.time.LocalDate.parse(end))),
                HttpStatus.OK);
    }

    @GetMapping(value = "/without-office", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Doctor>> withoutOffice() {
        return new ResponseEntity<>(doctorService.getDoctorsWithoutOffice(), HttpStatus.OK);
    }
}
