package lk.icbt.sunrise.dental.client.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentDto(
        String appointmentNumber,
        String patientName,
        String address,
        String contactNumber,
        String dentistName,
        String treatmentTypeName,
        LocalDate appointmentDate,
        LocalTime appointmentTime,
        String status
) {
}
