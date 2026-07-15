package lk.icbt.sunrise.dental.repository;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentTypeRepository extends JpaRepository<TreatmentType, Long> {
}
