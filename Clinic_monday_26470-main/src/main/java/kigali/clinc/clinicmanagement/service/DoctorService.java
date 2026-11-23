package kigali.clinc.clinicmanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Doctor;
import kigali.clinc.clinicmanagement.domain.Office;
import kigali.clinc.clinicmanagement.domain.Specialization;
import kigali.clinc.clinicmanagement.repository.DoctorRepository;
import kigali.clinc.clinicmanagement.repository.OfficeRepository;
import kigali.clinc.clinicmanagement.repository.SpecializationRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepo;
    private final OfficeRepository officeRepository;
    private final SpecializationRepository specializationRepo;

    public DoctorService(DoctorRepository doctorRepo,
                         OfficeRepository officeRepository,
                         SpecializationRepository specializationRepo) {
        this.doctorRepo = doctorRepo;
        this.officeRepository = officeRepository;
        this.specializationRepo = specializationRepo;
    }

    public String saveDoctor(Doctor doctor, int officeNumber) {
        if (officeNumber > 0) {
            Optional<Office> office = officeRepository.findByOfficeNumber(officeNumber);
            if (office.isEmpty()) {
                return "Office with number " + officeNumber + " not found";
            }
            doctor.setOffice(office.get());
        }
        if (doctorRepo.existsByFirstNameAndLastName(doctor.getFirstName(), doctor.getLastName())) {
            return "Doctor " + doctor.getFirstName() + " " + doctor.getLastName() + " already exists";
        }
        doctorRepo.save(doctor);
        return "Doctor is saved Successfully";
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }

    public Doctor getDoctorById(UUID id) {
        return doctorRepo.findById(id).orElse(null);
    }

    public String updateDoctor(UUID id, Doctor updatedDoctor) {
        Optional<Doctor> existing = doctorRepo.findById(id);
        if (existing.isEmpty()) {
            return "Doctor with id " + id + " not found";
        }
        Doctor doctor = existing.get();
        doctor.setFirstName(updatedDoctor.getFirstName());
        doctor.setLastName(updatedDoctor.getLastName());
        doctor.setDateOfBirth(updatedDoctor.getDateOfBirth());
        if (updatedDoctor.getOffice() != null) {
            Optional<Office> office = officeRepository.findByOfficeNumber(
                    updatedDoctor.getOffice().getOfficeNumber());
            if (office.isEmpty()) {
                return "Office with number "
                        + updatedDoctor.getOffice().getOfficeNumber() + " not found";
            }
            doctor.setOffice(office.get());
        }
        doctor.setSpecializations(updatedDoctor.getSpecializations());
        doctorRepo.save(doctor);
        return "Doctor updated Successfully";
    }

    public String deleteDoctor(UUID id) {
        if (!doctorRepo.existsById(id)) {
            return "Doctor with id " + id + " not found";
        }
        doctorRepo.deleteById(id);
        return "Doctor deleted Successfully";
    }

    public String assignSpecializations(UUID doctorId, List<UUID> specializationIds) {
        Optional<Doctor> existing = doctorRepo.findById(doctorId);
        if (existing.isEmpty()) {
            return "Doctor with id " + doctorId + " not found";
        }
        Doctor doctor = existing.get();
        List<Specialization> specs = specializationRepo.findAllById(specializationIds);
        if (specs.size() != specializationIds.size()) {
            return "One or more specializations not found";
        }
        for (Specialization spec : specs) {
            if (!doctor.getSpecializations().contains(spec)) {
                doctor.getSpecializations().add(spec);
            }
        }
        doctorRepo.save(doctor);
        return "Specializations assigned Successfully";
    }

    public String addSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }
        Doctor doctor = doctorOpt.get();
        Specialization spec = specOpt.get();
        if (doctor.getSpecializations().contains(spec)) {
            return "The doctor already has that specialization";
        }
        doctor.getSpecializations().add(spec);
        doctorRepo.save(doctor);
        return "Specialization added to the doctor";
    }

    public String removeSpecialization(UUID doctorId, UUID specializationId) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Optional<Specialization> specOpt = specializationRepo.findById(specializationId);
        if (specOpt.isEmpty()) {
            return "That specialization does not exist";
        }
        Doctor doctor = doctorOpt.get();
        Specialization spec = specOpt.get();
        if (!doctor.getSpecializations().contains(spec)) {
            return "The doctor does not have that specialization";
        }
        doctor.getSpecializations().remove(spec);
        doctorRepo.save(doctor);
        return "Specialization removed from the doctor";
    }

    public String changeOffice(UUID doctorId, int officeNumber) {
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return "The doctor with that id does not exist";
        }
        Optional<Office> officeOpt = officeRepository.findByOfficeNumber(officeNumber);
        if (officeOpt.isEmpty()) {
            return "The office with that office number does not exist";
        }
        Office newOffice = officeOpt.get();
        if (Boolean.TRUE.equals(doctorRepo.existsByOfficeIdAndIdNot(newOffice.getId(), doctorId))) {
            return "That office is taken by other doctor";
        }
        Doctor doctor = doctorOpt.get();
        doctor.setOffice(newOffice);
        doctorRepo.save(doctor);
        return "Office changed successfully";
    }

    public List<Doctor> getDoctorsByFirstName(String firstName) {
        return doctorRepo.findByFirstNameIgnoreCase(firstName);
    }

    public List<Object[]> countDoctorsBySpecialization(String specName) {
        return doctorRepo.countDoctorsBySpecialization(specName);
    }

    public List<Doctor> getDoctorsWithoutAppointments() {
        return doctorRepo.findDoctorsWithoutAppointments();
    }

    public List<Doctor> getDoctorsBySpecialization(String name) {
        return doctorRepo.findDoctorsBySpecializationName(name);
    }

    public List<Doctor> getDoctorsWithoutOffice() {
        return doctorRepo.findDoctorsWithoutOffice();
    }

    public List<Doctor> searchDoctorsByName(String name) {
        return doctorRepo.searchDoctorsByName(name);
    }

    public List<Object[]> getDoctorWorkload(java.sql.Date start, java.sql.Date end) {
        return doctorRepo.findDoctorWorkload(
                start, end, kigali.clinc.clinicmanagement.domain.AppointmentStatus.COMPLETED);
    }
}
