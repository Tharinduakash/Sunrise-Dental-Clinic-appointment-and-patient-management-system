package lk.icbt.sunrise.dental.exception;

public class DoubleBookingException extends RuntimeException {
    public DoubleBookingException(String message) {
        super(message);
    }
}
