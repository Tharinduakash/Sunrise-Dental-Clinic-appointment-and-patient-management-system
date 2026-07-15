package lk.icbt.sunrise.dental.service.billing;

import lk.icbt.sunrise.dental.domain.TreatmentType;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Factory pattern: resolves the correct {@link BillingStrategy} for a given
 * treatment type at runtime, so callers never need to know the concrete
 * strategy classes. {@link StandardBillingStrategy} is always injected
 * separately and used only as the last-resort fallback, since it would
 * otherwise match every treatment type.
 */
@Component
public class BillingStrategyFactory {

    private final List<BillingStrategy> specificStrategies;
    private final StandardBillingStrategy fallbackStrategy;

    public BillingStrategyFactory(List<BillingStrategy> strategies, StandardBillingStrategy fallbackStrategy) {
        this.fallbackStrategy = fallbackStrategy;
        this.specificStrategies = strategies.stream()
                .filter(strategy -> !(strategy instanceof StandardBillingStrategy))
                .toList();
    }

    public BillingStrategy resolve(TreatmentType treatmentType) {
        return specificStrategies.stream()
                .filter(strategy -> strategy.supports(treatmentType))
                .findFirst()
                .orElse(fallbackStrategy);
    }
}
