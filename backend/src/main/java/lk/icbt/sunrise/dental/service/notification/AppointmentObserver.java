package lk.icbt.sunrise.dental.service.notification;

import lk.icbt.sunrise.dental.domain.Appointment;

/**
 * Observer pattern: any number of independent notification channels can react
 * to an appointment being created without the appointment service knowing
 * about them individually.
 */
public interface AppointmentObserver {
    void onAppointmentCreated(Appointment appointment);
}
