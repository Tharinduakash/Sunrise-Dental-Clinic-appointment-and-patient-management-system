package lk.icbt.sunrise.dental.client.dto;

import java.math.BigDecimal;

public record TreatmentTypeDto(Long id, String name, BigDecimal baseCost, BigDecimal consultationFee) {
    @Override
    public String toString() {
        return name + " (Rs. " + baseCost + " + Rs. " + consultationFee + " consultation)";
    }
}
