package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SurgicalBillingStrategyTest {

    private final SurgicalBillingStrategy strategy = new SurgicalBillingStrategy();

    @Test
    void supports_returnsTrue_forExtractionAndRootCanal() {
        assertTrue(strategy.supports(new TreatmentType("Tooth Extraction", new BigDecimal("5000.00"), new BigDecimal("1500.00"))));
        assertTrue(strategy.supports(new TreatmentType("Root Canal Treatment", new BigDecimal("15000.00"), new BigDecimal("2000.00"))));
    }

    @Test
    void supports_returnsFalse_forNonSurgicalTreatment() {
        assertFalse(strategy.supports(new TreatmentType("General Checkup", BigDecimal.ZERO, new BigDecimal("1500.00"))));
    }

    @Test
    void calculateTreatmentCost_appliesTenPercentSurcharge() {
        TreatmentType extraction = new TreatmentType("Tooth Extraction", new BigDecimal("5000.00"), new BigDecimal("1500.00"));

        BigDecimal cost = strategy.calculateTreatmentCost(extraction);

        assertEquals(new BigDecimal("5500.00"), cost);
    }
}
