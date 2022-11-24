package ewm.other;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ewm.exception.EntityNotFoundExc;
import ewm.exception.MainPropDuplicateExc;
import ewm.exception.ValidationExc;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExc(final ValidationExc e) {
        return ErrorResponse.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason(e.getReason())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ErrorResponse handleEntityNotFoundExc(final EntityNotFoundExc e) {
        return ErrorResponse.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason(e.getReason())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    private ErrorResponse handleMainPropDuplicateExc(final MainPropDuplicateExc e) {
        return ErrorResponse.builder()
                .errors(e.getStackTrace())
                .message(e.getMessage())
                .reason(e.getReason())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
