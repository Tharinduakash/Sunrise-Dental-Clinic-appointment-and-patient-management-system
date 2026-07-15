package lk.icbt.sunrise.dental.dto;

import lk.icbt.sunrise.dental.domain.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponse(
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
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getAppointmentNumber(),
                appointment.getPatient().getName(),
                appointment.getPatient().getAddress(),
                appointment.getPatient().getContactNumber(),
                appointment.getDentist().getName(),
                appointment.getTreatmentType().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getStatus().name()
        );
    }
}
