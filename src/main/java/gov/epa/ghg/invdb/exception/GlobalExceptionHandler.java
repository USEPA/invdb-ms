package gov.epa.ghg.invdb.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {
        @Value("${spring.servlet.multipart.max-file-size}")
        private String maxFileSize;

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleGenericException(HttpServletRequest request, Exception ex) {
                log.error(ex);
                ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                ex.getMessage(), request.getRequestURI());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(error);
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiError> handleBadRequestException(HttpServletRequest request,
                        BadRequestException e) {
                log.error("Bad request: ", e);
                ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                e.getMessage(), request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        @ExceptionHandler(ResponseException.class)
        public ResponseEntity<ApiError> handleResponseException(HttpServletRequest request,
                        ResponseException e) {
                ApiError error = new ApiError(e.getStatus().value(),
                                e.getStatus().getReasonPhrase(),
                                e.getMessage(), request.getRequestURI());
                return ResponseEntity.status(e.getStatus())
                                .body(error);
        }

        @ExceptionHandler(PythonServiceException.class)
        public ResponseEntity<ApiError> handlePythonServiceException(HttpServletRequest request,
                        PythonServiceException ex) {
                log.error("Python service error: ", ex);
                Throwable cause = ex.getCause();
                ApiError error;
                if (cause instanceof ResourceAccessException) {
                        error = new ApiError(HttpStatus.GATEWAY_TIMEOUT.value(),
                                        HttpStatus.GATEWAY_TIMEOUT.getReasonPhrase(),
                                        "Python service is unreachable. Call system admin.", request.getRequestURI());
                        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(error);
                } else if (cause instanceof WebClientResponseException webEx) {
                        String traceback = extractTracebackFromJson(webEx.getResponseBodyAsString());
                        log.error("traceback: ", traceback);
                        error = new ApiError(webEx.getStatusCode().value(),
                                        webEx.getStatusText(),
                                        "Python service error: " + traceback,
                                        request.getRequestURI());
                        return ResponseEntity.status(webEx.getStatusCode()).body(error);
                } else {
                        error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                                        "Unknown error communicating with Python service",
                                        request.getRequestURI());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(error);
                }

        }

        @ExceptionHandler(ExcelReadException.class)
        public ResponseEntity<ApiError> handleExcelReadException(HttpServletRequest request,
                        ExcelReadException e) {
                ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(),
                                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                e.getMessage(), request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(error);
        }

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public ResponseEntity<ApiError> handleMaxUploadSizeExceededException(HttpServletRequest request,
                        MaxUploadSizeExceededException ex) {
                ApiError error = new ApiError(
                                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                                "File size exceeds the maximum allowed size of " + maxFileSize,
                                request.getRequestURI());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        private String extractTracebackFromJson(String errorBody) {
                if (errorBody == null || errorBody.isBlank()) {
                        return "No traceback returned from Python service.";
                }
                try {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(errorBody);
                        if (root.has("traceback")) {
                                return root.get("traceback").asText();
                        }
                } catch (JsonProcessingException e) {
                        return "Non-JSON error returned: " + errorBody;
                }
                return errorBody;
        }

}
