package clean_code.seminar_registration.exception;

public class DuplicateUserRegistrationException extends RuntimeException {
    public DuplicateUserRegistrationException(final String message) {
        super(message);
    }
}
