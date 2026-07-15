package lk.icbt.sunrise.dental.service.notification;

import lk.icbt.sunrise.dental.domain.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simulated notification channel: logs a message in place of an actual SMS/email
 * gateway integration. Kept as a separate observer so a real provider (e.g. an
 * SMTP or SMS API client) can be added later by simply registering another
 * {@link AppointmentObserver} bean, with no change to the appointment service.
 */
@Component
public class ConsoleNotificationObserver implements AppointmentObserver {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationObserver.class);

    @Override
    public void onAppointmentCreated(Appointment appointment) {
        log.info("[SIMULATED SMS/EMAIL] Dear {}, your appointment {} with {} is confirmed for {} at {}.",
                appointment.getPatient().getName(),
                appointment.getAppointmentNumber(),
                appointment.getDentist().getName(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime());
    }
}
