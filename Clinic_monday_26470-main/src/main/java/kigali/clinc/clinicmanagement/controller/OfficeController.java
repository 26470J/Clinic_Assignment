package kigali.clinc.clinicmanagement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.service.OfficeService;

@RestController
@RequestMapping(value = "/api/offices")
@CrossOrigin(origins = "http://localhost:5173") 
public class OfficeController {

    private final OfficeService officeServes;

    public OfficeController(OfficeService officeServes) {
        this.officeServes = officeServes;
    }

    @PostMapping(value = "/save",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveOffice(@RequestBody Office office) {
        String msg = officeServes.saveOffice(office);
        return msg.equalsIgnoreCase("Office is saved Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.CREATED)
                : new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getAllOffices() {
        return new ResponseEntity<>(officeServes.getAllOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOfficeById(@PathVariable UUID id) {
        Office office = officeServes.getOfficeById(id);
        return office == null
                ? new ResponseEntity<>("Office not found", HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(office, HttpStatus.OK);
    }

    @GetMapping(value = "/number/{officeNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getOfficeByNumber(@PathVariable int officeNumber) {
        Office office = officeServes.getOfficeByNumber(officeNumber);
        return office == null
                ? new ResponseEntity<>("Office with number " + officeNumber + " not found", HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(office, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateOffice(@PathVariable UUID id, @RequestBody Office office) {
        String msg = officeServes.updateOffice(id, office);
        if (msg.equalsIgnoreCase("Office updated Successfully")) {
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } else if (msg.contains("not found")) {
            return new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteOffice(@PathVariable UUID id) {
        String msg = officeServes.deleteOffice(id);
        return msg.equalsIgnoreCase("Office deleted Successfully")
                ? new ResponseEntity<>(msg, HttpStatus.OK)
                : new ResponseEntity<>(msg, HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> searchByName(@RequestParam String name) {
        return new ResponseEntity<>(officeServes.searchOfficesByName(name), HttpStatus.OK);
    }

    @GetMapping(value = "/free", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Office>> getFreeOffices() {
        return new ResponseEntity<>(officeServes.getFreeOffices(), HttpStatus.OK);
    }

    @GetMapping(value = "/busiest", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> busiest() {
        return new ResponseEntity<>(officeServes.getBusiestOffice(), HttpStatus.OK);
    }
}
