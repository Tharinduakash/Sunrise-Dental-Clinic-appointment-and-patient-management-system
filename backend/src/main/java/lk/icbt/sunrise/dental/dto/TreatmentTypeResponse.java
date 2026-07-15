package lk.icbt.sunrise.dental.dto;

import lk.icbt.sunrise.dental.domain.TreatmentType;

import java.math.BigDecimal;

public record TreatmentTypeResponse(Long id, String name, BigDecimal baseCost, BigDecimal consultationFee) {
    public static TreatmentTypeResponse from(TreatmentType treatmentType) {
        return new TreatmentTypeResponse(
                treatmentType.getId(),
                treatmentType.getName(),
                treatmentType.getBaseCost(),
                treatmentType.getConsultationFee()
        );
    }
}
