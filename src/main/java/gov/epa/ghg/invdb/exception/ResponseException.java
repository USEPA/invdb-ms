package gov.epa.ghg.invdb.exception;

import org.springframework.http.HttpStatus;

public class ResponseException extends RuntimeException {
    private final HttpStatus status;

    public ResponseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
