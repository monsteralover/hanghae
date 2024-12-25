package clean_code.seminar_registration;

import clean_code.seminar_registration.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getBindingResult().getFieldError().getDefaultMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DateFormatMalformedException.class)
    public ResponseEntity<ErrorResponse> handleDateFormatMalformedException(DateFormatMalformedException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(DuplicateUserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUserRegistrationException(DuplicateUserRegistrationException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(LectureDoesNotExistException.class)
    public ResponseEntity<ErrorResponse> handleLectureDoesNotExistException(LectureDoesNotExistException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(MaxEnrollmentExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxEnrollmentExceededException(MaxEnrollmentExceededException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(errorResponse);
    }


}
