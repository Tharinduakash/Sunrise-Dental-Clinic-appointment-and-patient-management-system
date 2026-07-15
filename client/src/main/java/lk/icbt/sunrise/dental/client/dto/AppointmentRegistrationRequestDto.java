package lk.icbt.sunrise.dental.client.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRegistrationRequestDto(
        String patientName,
        String address,
        String contactNumber,
        Long dentistId,
        Long treatmentTypeId,
        LocalDate appointmentDate,
        LocalTime appointmentTime
) {
}
