package clean_code.seminar_registration.exception;

public class InvalidLectureDateException extends RuntimeException {
    public InvalidLectureDateException(final String message) {
        super(message);
    }
}
