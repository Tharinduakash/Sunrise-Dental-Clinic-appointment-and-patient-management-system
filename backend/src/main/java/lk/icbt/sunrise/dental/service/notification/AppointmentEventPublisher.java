package lk.icbt.sunrise.dental.service.notification;

import lk.icbt.sunrise.dental.domain.Appointment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Observer pattern (subject side): fans an appointment-created event out to
 * every registered {@link AppointmentObserver} bean.
 */
@Component
public class AppointmentEventPublisher {

    private final List<AppointmentObserver> observers;

    public AppointmentEventPublisher(List<AppointmentObserver> observers) {
        this.observers = observers;
    }

    public void publishAppointmentCreated(Appointment appointment) {
        observers.forEach(observer -> observer.onAppointmentCreated(appointment));
    }
}
