package clean_code.seminar_registration.exception;

public class DateFormatMalformedException extends RuntimeException {
    public DateFormatMalformedException(final String message) {
        super(message);
    }
}
