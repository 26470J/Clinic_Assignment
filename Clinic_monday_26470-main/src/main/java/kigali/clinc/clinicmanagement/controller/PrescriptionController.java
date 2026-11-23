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

import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.service.PrescriptionService;

@RestController
@RequestMapping(value = "/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @PostMapping(value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> save(@RequestBody Prescription prescription,
                                  @RequestParam UUID appointmentId) {
        String msg = prescriptionService.savePrescription(prescription, appointmentId);
        if (msg.equalsIgnoreCase("The appointment with that id does not exist"))
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        if (msg.equalsIgnoreCase("Prescriptions are only for COMPLETED appointments"))
            return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @GetMapping(value = "/appointment/{appointmentId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ofAppointment(@PathVariable UUID appointmentId) {
        if (!prescriptionService.appointmentExists(appointmentId)) {
            return new ResponseEntity<>("The appointment with that id does not exist",
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(
                prescriptionService.getPrescriptionsOfAppointment(appointmentId),
                HttpStatus.OK);
    }

    @GetMapping(value = "/patient/{ssNumber}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Prescription>> ofPatient(@PathVariable String ssNumber) {
        return new ResponseEntity<>(
                prescriptionService.getHistoryOfPatient(ssNumber),
                HttpStatus.OK);
    }

    @GetMapping(value = "/stats/top-medicines", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object[]>> topMedicines() {
        return new ResponseEntity<>(prescriptionService.getTopMedicines(), HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @RequestBody Prescription prescription) {
        String msg = prescriptionService.updatePrescription(id, prescription);
        return msg.equalsIgnoreCase("Prescription updated successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        String msg = prescriptionService.deletePrescription(id);
        return msg.equalsIgnoreCase("Prescription deleted successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }
}
