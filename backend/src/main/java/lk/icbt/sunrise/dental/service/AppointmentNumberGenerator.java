package lk.icbt.sunrise.dental.service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Classic Singleton pattern (deliberately implemented by hand rather than relying
 * on Spring's default singleton bean scope): it guards a single shared counter used
 * to hand out unique, sequential appointment numbers across the whole application,
 * so exactly one instance may ever exist regardless of how it is accessed.
 */
public final class AppointmentNumberGenerator {

    private static final String PREFIX = "APT-";
    private static volatile AppointmentNumberGenerator instance;

    private final AtomicLong counter;

    private AppointmentNumberGenerator(long startValue) {
        this.counter = new AtomicLong(startValue);
    }

    public static synchronized AppointmentNumberGenerator initialize(long startValue) {
        if (instance == null) {
            instance = new AppointmentNumberGenerator(startValue);
        }
        return instance;
    }

    public static AppointmentNumberGenerator getInstance() {
        if (instance == null) {
            throw new IllegalStateException("AppointmentNumberGenerator.initialize(...) must be called before use");
        }
        return instance;
    }

    public String next() {
        return PREFIX + String.format("%06d", counter.incrementAndGet());
    }
}
