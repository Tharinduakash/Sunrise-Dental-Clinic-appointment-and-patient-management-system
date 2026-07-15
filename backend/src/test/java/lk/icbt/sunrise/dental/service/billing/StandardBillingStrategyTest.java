package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardBillingStrategyTest {

    private final StandardBillingStrategy strategy = new StandardBillingStrategy();

    @Test
    void supports_returnsTrue_forAnyTreatmentType() {
        assertTrue(strategy.supports(new TreatmentType("Anything", BigDecimal.ONE, BigDecimal.ONE)));
    }

    @Test
    void calculateTreatmentCost_returnsBaseCostUnchanged() {
        TreatmentType scaling = new TreatmentType("Scaling and Polishing", new BigDecimal("3500.00"), new BigDecimal("1500.00"));

        assertEquals(new BigDecimal("3500.00"), strategy.calculateTreatmentCost(scaling));
    }
}
