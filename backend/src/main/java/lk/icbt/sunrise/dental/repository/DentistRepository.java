package lk.icbt.sunrise.dental.repository;

import lk.icbt.sunrise.dental.domain.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DentistRepository extends JpaRepository<Dentist, Long> {
}
