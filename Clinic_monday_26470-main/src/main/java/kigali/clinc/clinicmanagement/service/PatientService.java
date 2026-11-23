package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Patient;
import kigali.clinc.clinicmanagement.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepo;

    public PatientService(PatientRepository patientRepo) {
        this.patientRepo = patientRepo;
    }

    public String savePatient(Patient patient) {
        if (patientRepo.existsByFirstNameAndLastName(patient.getFirstName(), patient.getLastName())) {
            return "Patient " + patient.getFirstName() + " " + patient.getLastName() + " already exists";
        }
        patientRepo.save(patient);
        return "Patient is saved Successfully";
    }

    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Patient getPatientById(UUID id) {
        return patientRepo.findById(id).orElse(null);
    }

    public String updatePatient(UUID id, Patient updatedPatient) {
        Optional<Patient> existing = patientRepo.findById(id);
        if (existing.isEmpty()) {
            return "Patient with id " + id + " not found";
        }
        Patient patient = existing.get();
        patient.setFirstName(updatedPatient.getFirstName());
        patient.setLastName(updatedPatient.getLastName());
        patient.setDateOfBirth(updatedPatient.getDateOfBirth());
        patientRepo.save(patient);
        return "Patient updated Successfully";
    }

    public String deletePatient(UUID id) {
        if (!patientRepo.existsById(id)) {
            return "Patient with id " + id + " not found";
        }
        patientRepo.deleteById(id);
        return "Patient deleted Successfully";
    }

    public List<Patient> searchPatientsByLastName(String fragment) {
        return patientRepo.findByLastNameContainingIgnoreCase(fragment);
    }

    public List<Patient> getPatientsByLastName(String lastName) {
        return patientRepo.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    public List<Patient> getPatientsOfDoctor(UUID doctorId) {
        return patientRepo.findPatientsOfDoctor(doctorId);
    }

    public boolean patientSsNumberExists(String ssNumber) {
        return patientRepo.existsBySsNumber(ssNumber);
    }

    public List<Patient> getFrequentPatients(long min) {
        return patientRepo.findFrequentPatients(min);
    }
}
