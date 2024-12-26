package clean_code.seminar_registration.exception;

public class LectureDoesNotExistException extends RuntimeException {
    public LectureDoesNotExistException(final String message) {
        super(message);
    }
}
