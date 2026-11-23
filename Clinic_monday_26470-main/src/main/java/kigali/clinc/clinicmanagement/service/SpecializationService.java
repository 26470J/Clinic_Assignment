package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class SpecializationService {

    private final SpecializationRepository specializationRepo;

    public SpecializationService(SpecializationRepository specializationRepo) {
        this.specializationRepo = specializationRepo;
    }

    public String saveSpecialization(Specialization spec) {
        if (specializationRepo.existsByName(spec.getName())) {
            return "Specialization " + spec.getName() + " already exists";
        }
        specializationRepo.save(spec);
        return "Specialization is saved Successfully";
    }

    public List<Specialization> getAllSpecializations() {
        return specializationRepo.findAll();
    }

    public Specialization getSpecializationById(UUID id) {
        return specializationRepo.findById(id).orElse(null);
    }

    public List<Specialization> searchByName(String fragment) {
        return specializationRepo.findByNameContainingIgnoreCase(fragment);
    }

    public String updateSpecialization(UUID id, Specialization updated) {
        Optional<Specialization> existing = specializationRepo.findById(id);
        if (existing.isEmpty()) {
            return "Specialization with id " + id + " not found";
        }
        Specialization spec = existing.get();
        if (!spec.getName().equalsIgnoreCase(updated.getName())
                && specializationRepo.existsByName(updated.getName())) {
            return "Specialization " + updated.getName() + " already exists";
        }
        spec.setName(updated.getName());
        specializationRepo.save(spec);
        return "Specialization updated Successfully";
    }

    public String deleteSpecialization(UUID id) {
        if (!specializationRepo.existsById(id)) {
            return "Specialization with id " + id + " not found";
        }
        specializationRepo.deleteById(id);
        return "Specialization deleted Successfully";
    }

    public List<Specialization> getUnusedSpecializations() {
        return specializationRepo.findUnusedSpecializations();
    }
}
