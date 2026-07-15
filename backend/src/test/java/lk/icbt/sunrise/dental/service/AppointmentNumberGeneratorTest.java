package lk.icbt.sunrise.dental.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppointmentNumberGeneratorTest {

    @Test
    void next_producesIncreasingNumbersMatchingTheExpectedFormat() {
        AppointmentNumberGenerator.initialize(0);
        AppointmentNumberGenerator generator = AppointmentNumberGenerator.getInstance();

        String first = generator.next();
        String second = generator.next();

        assertTrue(first.matches("APT-\\d{6}"), "Unexpected format: " + first);
        assertTrue(second.matches("APT-\\d{6}"), "Unexpected format: " + second);
        assertNotEquals(first, second);
    }
}
