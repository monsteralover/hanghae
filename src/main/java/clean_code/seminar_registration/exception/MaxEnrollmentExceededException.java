package clean_code.seminar_registration.exception;

public class MaxEnrollmentExceededException extends RuntimeException {
    public MaxEnrollmentExceededException(final String message) {
        super(message);
    }
}
