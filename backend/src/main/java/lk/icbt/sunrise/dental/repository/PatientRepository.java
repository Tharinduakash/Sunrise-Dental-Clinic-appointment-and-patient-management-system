package lk.icbt.sunrise.dental.repository;

import lk.icbt.sunrise.dental.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByContactNumber(String contactNumber);
    List<Patient> findByNameContainingIgnoreCase(String name);
}
