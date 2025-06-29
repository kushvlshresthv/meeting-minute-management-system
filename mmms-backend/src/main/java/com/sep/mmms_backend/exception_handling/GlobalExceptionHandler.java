package com.sep.mmms_backend.exception_handling;


import com.sep.mmms_backend.exceptions.PasswordChangeNotAllowedException;
import com.sep.mmms_backend.exceptions.UnauthorizedUpdateException;
import com.sep.mmms_backend.exceptions.UserDoesNotExist;
import com.sep.mmms_backend.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserDoesNotExist.class)
    public ResponseEntity<Response> handleUserDoesNotExist(UserDoesNotExist ex) {
        log.error("Exception handled: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedUpdateException.class)
    public ResponseEntity<Response> handleUnauthorizedUpdate(UnauthorizedUpdateException ex) {
        log.error("Exception handled: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(PasswordChangeNotAllowedException.class)
    public ResponseEntity<Response> handlePasswordChangeNotAllowed(PasswordChangeNotAllowedException ex) {
        log.error("Exception handled: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(Exception e) {
        return ResponseEntity.internalServerError().body(null);
    }
}
