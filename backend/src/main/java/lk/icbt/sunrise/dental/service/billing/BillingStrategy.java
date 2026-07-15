package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;

import java.math.BigDecimal;

/**
 * Strategy pattern: how a treatment's cost is calculated varies by category
 * (a straightforward checkup differs from a surgical procedure or an
 * orthodontic consultation), so the calculation is delegated to an
 * interchangeable strategy rather than branching inside the billing service.
 */
public interface BillingStrategy {

    boolean supports(TreatmentType treatmentType);

    BigDecimal calculateTreatmentCost(TreatmentType treatmentType);
}
