package kigali.clinc.clinicmanagement.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kigali.clinc.clinicmanagement.domain.Doctor;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

    Optional<Doctor> findByFirstNameAndLastName(String firstName, String lastName);

    List<Doctor> findByLastName(String lastName);

    List<Doctor> findByFirstNameContainingIgnoreCaseOrderByFirstNameAsc(String fragment);

    Boolean existsByFirstNameAndLastName(String firstName, String lastName);

    Boolean existsByOfficeIdAndIdNot(UUID officeId, UUID doctorId);

    List<Doctor> findByFirstNameIgnoreCase(String firstName);

    @Query("SELECT s.name, COUNT(d) " +
           "FROM Doctor d JOIN d.specializations s " +
           "WHERE s.name = :name " +
           "GROUP BY s.name")
    List<Object[]> countDoctorsBySpecialization(@Param("name") String name);

    @Query("SELECT d FROM Doctor d WHERE d.appointments IS EMPTY")
    List<Doctor> findDoctorsWithoutAppointments();

    @Query("SELECT d FROM Doctor d JOIN d.specializations s " +
           "WHERE LOWER(s.name) = LOWER(:name)")
    List<Doctor> findDoctorsBySpecializationName(@Param("name") String name);

    @Query("SELECT d FROM Doctor d WHERE d.office IS NULL ORDER BY d.lastName ASC")
    List<Doctor> findDoctorsWithoutOffice();

    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "   OR LOWER(d.lastName)  LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY d.lastName ASC")
    List<Doctor> searchDoctorsByName(@Param("name") String name);

    @Query("SELECT d.firstName, d.lastName, COUNT(a) " +
           "FROM Doctor d JOIN d.appointments a " +
           "WHERE a.status = :status " +
           "  AND a.appointmentDate BETWEEN :start AND :end " +
           "GROUP BY d.id, d.firstName, d.lastName " +
           "ORDER BY COUNT(a) DESC")
    List<Object[]> findDoctorWorkload(@Param("start") java.sql.Date start,
                                      @Param("end") java.sql.Date end,
                                      @Param("status") kigali.clinc.clinicmanagement.domain.AppointmentStatus status);
}
