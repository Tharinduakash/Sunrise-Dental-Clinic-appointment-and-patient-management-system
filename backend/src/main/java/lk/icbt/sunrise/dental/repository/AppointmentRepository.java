package lk.icbt.sunrise.dental.repository;

import lk.icbt.sunrise.dental.domain.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    boolean existsByDentist_IdAndAppointmentDateAndAppointmentTime(Long dentistId, LocalDate date, LocalTime time);

    // open-in-view is disabled, so callers that map the result to a DTO after the service method
    // returns need patient/dentist/treatmentType already loaded - hence the explicit join fetch.
    @Query("select a from Appointment a " +
            "join fetch a.patient join fetch a.dentist join fetch a.treatmentType " +
            "where a.appointmentNumber = :appointmentNumber")
    Optional<Appointment> findByAppointmentNumberWithDetails(@Param("appointmentNumber") String appointmentNumber);

    @Query("select a from Appointment a " +
            "join fetch a.patient join fetch a.dentist join fetch a.treatmentType " +
            "where a.appointmentDate = :date order by a.appointmentTime asc")
    List<Appointment> findByAppointmentDateWithDetails(@Param("date") LocalDate date);
}
