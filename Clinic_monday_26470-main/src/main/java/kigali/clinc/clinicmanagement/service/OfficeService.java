package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;

@Service
public class OfficeService {

    private final OfficeRepository officeRepo;

    public OfficeService(OfficeRepository officeRepo) {
        this.officeRepo = officeRepo;
    }

    public String saveOffice(Office office) {
        if (officeRepo.existsByOfficeNumber(office.getOfficeNumber())) {
            return "Office with that number " + office.getOfficeNumber() + " already exists";
        }
        officeRepo.save(office);
        return "Office is saved Successfully";
    }

    public List<Office> getAllOffices() {
        return officeRepo.findAll();
    }

    public Office getOfficeById(UUID id) {
        return officeRepo.findById(id).orElse(null);
    }

    public Office getOfficeByNumber(int officeNumber) {
        return officeRepo.findByOfficeNumber(officeNumber).orElse(null);
    }

    public String updateOffice(UUID id, Office updatedOffice) {
        Optional<Office> existing = officeRepo.findById(id);
        if (existing.isEmpty()) {
            return "Office with id " + id + " not found";
        }
        Office office = existing.get();
        if (office.getOfficeNumber() != updatedOffice.getOfficeNumber()
                && officeRepo.existsByOfficeNumber(updatedOffice.getOfficeNumber())) {
            return "Office with number " + updatedOffice.getOfficeNumber() + " already exists";
        }
        office.setName(updatedOffice.getName());
        office.setOfficeNumber(updatedOffice.getOfficeNumber());
        officeRepo.save(office);
        return "Office updated Successfully";
    }

    public String deleteOffice(UUID id) {
        if (!officeRepo.existsById(id)) {
            return "Office with id " + id + " not found";
        }
        officeRepo.deleteById(id);
        return "Office deleted Successfully";
    }

    public List<Office> searchOfficesByName(String fragment) {
        return officeRepo.findByNameContainingIgnoreCase(fragment);
    }

    public List<Office> getFreeOffices() {
        return officeRepo.findFreeOffices();
    }

    public String getBusiestOffice() {
        List<Object[]> rows = officeRepo.findBusiestOffice();
        if (rows.isEmpty()) return "No appointments yet";
        Object[] top = rows.get(0);
        return "Office: " + top[0] + " (number " + top[1] + ") with " + top[2] + " appointments";
    }
}
