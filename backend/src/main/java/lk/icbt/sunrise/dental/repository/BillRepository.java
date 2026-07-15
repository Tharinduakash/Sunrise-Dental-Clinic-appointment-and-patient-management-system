package lk.icbt.sunrise.dental.repository;

import lk.icbt.sunrise.dental.domain.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BillRepository extends JpaRepository<Bill, Long> {

    // open-in-view is disabled: eagerly fetch the appointment chain so BillResponse.from(...)
    // can be built safely after this service method returns.
    @Query("select b from Bill b " +
            "join fetch b.appointment a join fetch a.patient join fetch a.dentist join fetch a.treatmentType " +
            "where a.id = :appointmentId")
    Optional<Bill> findByAppointment_IdWithDetails(@Param("appointmentId") Long appointmentId);

    List<Bill> findByGeneratedAtBetween(LocalDateTime from, LocalDateTime to);
}
