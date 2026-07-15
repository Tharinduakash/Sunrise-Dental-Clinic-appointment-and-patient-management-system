package lk.icbt.sunrise.dental.client.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillDto(
        String appointmentNumber,
        String patientName,
        String treatmentTypeName,
        BigDecimal consultationFee,
        BigDecimal treatmentCost,
        BigDecimal totalAmount,
        LocalDateTime generatedAt
) {
}
