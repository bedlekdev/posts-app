package com.example.posts.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllException(Exception ex, WebRequest request) {
        return getErrorResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ErrorResponse> handleResourceNotFoundException(Exception ex, WebRequest request) {
        return getErrorResponseEntity(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotChangedException.class)
    public final ResponseEntity<ErrorResponse> handleResourceNotChangedException(Exception ex, WebRequest request) {
        return getErrorResponseEntity(ex, request, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        StringBuilder sb = new StringBuilder("Total Errors: ").append(ex.getErrorCount());
        ex.getFieldErrors().forEach(err -> sb.append(", field:  ")
                .append(err.getField())
                .append(", error message: ")
                .append(err.getDefaultMessage()));
        return new ResponseEntity<>(getErrorResponse(sb.toString(), request, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<ErrorResponse> getErrorResponseEntity(Exception ex, WebRequest request, HttpStatus httpStatus) {
        return new ResponseEntity<>(getErrorResponse(ex.getMessage(), request, httpStatus), httpStatus);

    }
    private static ErrorResponse getErrorResponse(String message, WebRequest request, HttpStatus httpStatus) {
        return new ErrorResponse(
                LocalDateTime.now(),
                message,
                request.getDescription(false),
                httpStatus.value());
    }
}
