package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

/**
 * Surgical procedures (extractions, root canals) carry a 10% facility/sterilisation
 * surcharge on top of the base treatment cost.
 */
@Component
public class SurgicalBillingStrategy implements BillingStrategy {

    private static final BigDecimal SURCHARGE_MULTIPLIER = new BigDecimal("1.10");
    private static final Set<String> SURGICAL_TREATMENTS = Set.of("Tooth Extraction", "Root Canal Treatment");

    @Override
    public boolean supports(TreatmentType treatmentType) {
        return SURGICAL_TREATMENTS.contains(treatmentType.getName());
    }

    @Override
    public BigDecimal calculateTreatmentCost(TreatmentType treatmentType) {
        return treatmentType.getBaseCost()
                .multiply(SURCHARGE_MULTIPLIER)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
