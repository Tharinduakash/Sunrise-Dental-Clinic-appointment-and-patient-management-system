package lk.icbt.sunrise.dental.dto;

import lk.icbt.sunrise.dental.domain.Bill;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponse(
        String appointmentNumber,
        String patientName,
        String treatmentTypeName,
        BigDecimal consultationFee,
        BigDecimal treatmentCost,
        BigDecimal totalAmount,
        LocalDateTime generatedAt
) {
    public static BillResponse from(Bill bill) {
        return new BillResponse(
                bill.getAppointment().getAppointmentNumber(),
                bill.getAppointment().getPatient().getName(),
                bill.getAppointment().getTreatmentType().getName(),
                bill.getConsultationFee(),
                bill.getTreatmentCost(),
                bill.getTotalAmount(),
                bill.getGeneratedAt()
        );
    }
}
