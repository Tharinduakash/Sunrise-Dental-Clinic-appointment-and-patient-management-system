package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Orthodontic consultations are billed at their base (consultation-only) cost;
 * the full braces treatment plan is quoted and billed separately once agreed.
 */
@Component
public class OrthodonticBillingStrategy implements BillingStrategy {

    private static final Set<String> ORTHODONTIC_TREATMENTS = Set.of("Braces Consultation");

    @Override
    public boolean supports(TreatmentType treatmentType) {
        return ORTHODONTIC_TREATMENTS.contains(treatmentType.getName());
    }

    @Override
    public BigDecimal calculateTreatmentCost(TreatmentType treatmentType) {
        return treatmentType.getBaseCost();
    }
}
