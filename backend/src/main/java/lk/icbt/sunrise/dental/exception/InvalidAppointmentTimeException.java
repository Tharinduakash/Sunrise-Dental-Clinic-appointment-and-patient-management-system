package lk.icbt.sunrise.dental.exception;

public class InvalidAppointmentTimeException extends RuntimeException {
    public InvalidAppointmentTimeException(String message) {
        super(message);
    }
}
