package com.valbermedeiros.dscatalog.resources.exceptions;

import com.valbermedeiros.dscatalog.services.exceptions.DataBaseException;
import com.valbermedeiros.dscatalog.services.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> entityNotFoundHandler(ResourceNotFoundException ex, HttpServletRequest request) {
        var status = HttpStatus.NOT_FOUND;
        var error = getStandardError(ex, status, request, "Resource not found");
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandardError> dataBaseExceptionHandler(DataBaseException ex, HttpServletRequest request) {
        var status = HttpStatus.BAD_REQUEST;
        var error = getStandardError(ex, status, request, "Database exception");
        return ResponseEntity.status(status).body(error);
    }

    private StandardError getStandardError(Exception ex, HttpStatus status, HttpServletRequest request, String errorMsg) {
        var error = new StandardError();
        error.setTimestamp(Instant.now());
        error.setStatus(status.value());
        error.setError(errorMsg);
        error.setMessage(ex.getMessage());
        error.setPath(request.getRequestURI());
        return error;
    }

}
