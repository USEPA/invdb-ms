package gov.epa.ghg.invdb.exception;

public class ExcelReadException extends RuntimeException {

    public ExcelReadException(String message) {
        super(message);
    }

    public ExcelReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
