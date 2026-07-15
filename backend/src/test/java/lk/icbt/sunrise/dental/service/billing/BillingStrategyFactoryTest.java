package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class BillingStrategyFactoryTest {

    private BillingStrategyFactory factory;

    @BeforeEach
    void setUp() {
        StandardBillingStrategy standard = new StandardBillingStrategy();
        List<BillingStrategy> allStrategies = List.of(standard, new SurgicalBillingStrategy(), new OrthodonticBillingStrategy());
        factory = new BillingStrategyFactory(allStrategies, standard);
    }

    @Test
    void resolve_picksSurgicalStrategy_forToothExtraction() {
        TreatmentType extraction = new TreatmentType("Tooth Extraction", new BigDecimal("5000.00"), new BigDecimal("1500.00"));

        assertInstanceOf(SurgicalBillingStrategy.class, factory.resolve(extraction));
    }

    @Test
    void resolve_picksOrthodonticStrategy_forBracesConsultation() {
        TreatmentType braces = new TreatmentType("Braces Consultation", new BigDecimal("2500.00"), new BigDecimal("2500.00"));

        assertInstanceOf(OrthodonticBillingStrategy.class, factory.resolve(braces));
    }

    @Test
    void resolve_fallsBackToStandardStrategy_forRoutineTreatment() {
        TreatmentType checkup = new TreatmentType("General Checkup", BigDecimal.ZERO, new BigDecimal("1500.00"));

        assertInstanceOf(StandardBillingStrategy.class, factory.resolve(checkup));
    }
}
