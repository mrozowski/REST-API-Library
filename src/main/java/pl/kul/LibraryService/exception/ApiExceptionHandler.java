package pl.kul.LibraryService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ServerErrorException;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {ApiRequestException.class})
    public ResponseEntity<Object> handlerApiRequestException(HttpServletRequest req, ApiRequestException e){
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;

        ApiException exception = new ApiException(
                ZonedDateTime.now(ZoneId.of("Z")),
                badRequest,
                e.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(exception, badRequest);
    }

    @ExceptionHandler(value = {ResourceAlreadyExists.class})
    public ResponseEntity<Object> handlerResourceAlreadyExistsException(HttpServletRequest req, ResourceAlreadyExists e){

        HttpStatus conflict = HttpStatus.CONFLICT;
        ApiException exception = new ApiException(
                ZonedDateTime.now(ZoneId.of("Z")),
                conflict,
                e.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(exception, conflict);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<Object> handlerResourceNotFoundException(HttpServletRequest req, ResourceNotFoundException e){

        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ApiException exception = new ApiException(
                ZonedDateTime.now(ZoneId.of("Z")),
                notFound,
                e.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(exception, notFound);
    }

    @ExceptionHandler(value = {InternalError.class})
    public ResponseEntity<Object> handlerServerErrorException(HttpServletRequest req, InternalError e){

        HttpStatus error = HttpStatus.INTERNAL_SERVER_ERROR;
        ApiException exception = new ApiException(
                ZonedDateTime.now(ZoneId.of("Z")),
                error,
                e.getMessage(),
                req.getRequestURI()
        );
        return new ResponseEntity<>(exception, error);
    }

}
