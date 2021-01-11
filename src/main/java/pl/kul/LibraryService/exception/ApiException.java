package pl.kul.LibraryService.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public class ApiException {
    private final ZonedDateTime timestamp;
    private final HttpStatus status;
    private final String message;
    private final String path;


    public ApiException(ZonedDateTime timestamp, HttpStatus httpStatus, String message, String path) {
        this.timestamp = timestamp;
        this.status = httpStatus;
        this.message = message;
        this.path = path;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
