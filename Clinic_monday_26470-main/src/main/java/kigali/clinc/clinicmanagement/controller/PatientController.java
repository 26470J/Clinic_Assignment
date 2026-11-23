package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.service.AppointmentService;
import kigali.clinc.clinicmanagement.service.DoctorService;
import kigali.clinc.clinicmanagement.service.PatientService;

@RestController
@RequestMapping(value = "/api/patients")
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentService appointmentService;

    public PatientController(PatientService patientService,
                             DoctorService doctorService,
                             AppointmentService appointmentService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @PostMapping(value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody Patient patient) {
        String msg = patientService.savePatient(patient);
        return msg.equalsIgnoreCase("Patient is saved Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.CREATED)
                : new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> all() {
        return new ResponseEntity<>(patientService.getAllPatients(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        Patient patient = patientService.getPatientById(id);
        return patient == null
                ? new ResponseEntity<>("Patient not found", HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(patient, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Patient patient) {
        String msg = patientService.updatePatient(id, patient);
        return msg.equalsIgnoreCase("Patient updated Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        String msg = patientService.deletePatient(id);
        return msg.equalsIgnoreCase("Patient deleted Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search/lastname", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> searchByLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(patientService.searchPatientsByLastName(lastName), HttpStatus.OK);
    }

    @GetMapping(value = "/by-last-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> byLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(patientService.getPatientsByLastName(lastName), HttpStatus.OK);
    }

    @GetMapping(value = "/of-doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ofDoctor(@PathVariable UUID doctorId) {
        if (doctorService.getDoctorById(doctorId) == null) {
            return new ResponseEntity<>("The doctor with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patientService.getPatientsOfDoctor(doctorId), HttpStatus.OK);
    }

    @GetMapping(value = "/{ssNumber}/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> upcoming(@PathVariable String ssNumber) {
        if (!patientService.patientSsNumberExists(ssNumber)) {
            return new ResponseEntity<>("That patient with that ssnumber does not exist",
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(
                appointmentService.getUpcomingForPatient(ssNumber),
                HttpStatus.OK);
    }

    @GetMapping(value = "/frequent", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> frequent(@RequestParam long min) {
        return new ResponseEntity<>(patientService.getFrequentPatients(min), HttpStatus.OK);
    }
}
