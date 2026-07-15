package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Default strategy for routine treatments (checkups, scaling, fillings):
 * the treatment cost is simply the base cost, no surcharge or discount.
 * Acts as the fallback when no other strategy claims a treatment type.
 */
@Component
public class StandardBillingStrategy implements BillingStrategy {

    @Override
    public boolean supports(TreatmentType treatmentType) {
        return true;
    }

    @Override
    public BigDecimal calculateTreatmentCost(TreatmentType treatmentType) {
        return treatmentType.getBaseCost();
    }
}
