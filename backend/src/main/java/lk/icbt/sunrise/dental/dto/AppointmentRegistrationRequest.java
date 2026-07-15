package lk.icbt.sunrise.dental.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRegistrationRequest(
        @NotBlank(message = "Patient name is required") String patientName,
        String address,
        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^[0-9+][0-9+\\- ]{6,19}$", message = "Contact number format is invalid")
        String contactNumber,
        @NotNull(message = "Dentist is required") Long dentistId,
        @NotNull(message = "Treatment type is required") Long treatmentTypeId,
        @NotNull(message = "Appointment date is required")
        @FutureOrPresent(message = "Appointment date cannot be in the past")
        LocalDate appointmentDate,
        @NotNull(message = "Appointment time is required") LocalTime appointmentTime
) {
}
