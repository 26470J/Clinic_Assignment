package kigali.clinc.clinicmanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import kigali.clinc.clinicmanagement.domain.Appointment;
import kigali.clinc.clinicmanagement.domain.AppointmentStatus;
import kigali.clinc.clinicmanagement.domain.Prescription;
import kigali.clinc.clinicmanagement.repository.AppointmentRepository;
import kigali.clinc.clinicmanagement.repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepo;
    private final AppointmentRepository appointmentRepo;

    public PrescriptionService(PrescriptionRepository prescriptionRepo,
                               AppointmentRepository appointmentRepo) {
        this.prescriptionRepo = prescriptionRepo;
        this.appointmentRepo = appointmentRepo;
    }

    public String savePrescription(Prescription prescription, UUID appointmentId) {
        Optional<Appointment> apptOpt = appointmentRepo.findById(appointmentId);
        if (apptOpt.isEmpty()) {
            return "The appointment with that id does not exist";
        }
        Appointment appointment = apptOpt.get();
        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            return "Prescriptions are only for COMPLETED appointments";
        }
        prescription.setIssuedDate(LocalDate.now());
        prescription.setAppointment(appointment);
        prescriptionRepo.save(prescription);
        return "Prescription saved successfully";
    }

    public boolean appointmentExists(UUID appointmentId) {
        return appointmentRepo.existsById(appointmentId);
    }

    public List<Prescription> getPrescriptionsOfAppointment(UUID appointmentId) {
        return prescriptionRepo.findByAppointmentId(appointmentId);
    }

    public List<Prescription> getHistoryOfPatient(String ssNumber) {
        return prescriptionRepo.findHistoryByPatientSsNumber(ssNumber);
    }

    public String updatePrescription(UUID id, Prescription updated) {
        Optional<Prescription> opt = prescriptionRepo.findById(id);
        if (opt.isEmpty()) {
            return "The prescription with that id does not exist";
        }
        Prescription p = opt.get();
        p.setMedicineName(updated.getMedicineName());
        p.setDosage(updated.getDosage());
        p.setDurationDays(updated.getDurationDays());
        prescriptionRepo.save(p);
        return "Prescription updated successfully";
    }

    public List<Object[]> getTopMedicines() {
        return prescriptionRepo.countByMedicineName();
    }

    public String deletePrescription(UUID id) {
        if (!prescriptionRepo.existsById(id)) {
            return "The prescription with that id does not exist";
        }
        prescriptionRepo.deleteById(id);
        return "Prescription deleted successfully";
    }
}
