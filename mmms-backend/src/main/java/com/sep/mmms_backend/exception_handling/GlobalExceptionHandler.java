package com.sep.mmms_backend.exception_handling;


import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<Response> handleUserDoesNotExist(UserDoesNotExistException ex) {
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

    //handles validation errors
    @ExceptionHandler(ValidationFailureException.class)
    public ResponseEntity<Object> handleConstraintViolation(ValidationFailureException ex) {
        Errors errors = ex.getErrors();

        HashMap<String, ArrayList<String>> errorMessages = new HashMap<>();
        errors.getFieldErrors().forEach(
                error-> {
                    //if the key is not already present in the Map, create the key as well as new ArrayList for the value
                    if(!errorMessages.containsKey(error.getField())){
                        ArrayList<String> list = new ArrayList<>();
                        list.add(error.getDefaultMessage());
                        errorMessages.put(error.getField(),list);
                    } else {
                        errorMessages.get(error.getField()).add(error.getDefaultMessage());
                    }
                }
        );

       log.error("Exception Handled: {}: {}", ex.getMessage(), errorMessages);
       return ResponseEntity.badRequest().body(new Response(ex.getMessage(), errorMessages));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Response> usernameAlreadyExists(UsernameAlreadyExistsException ex) {
        log.error("User with the username `{}` already exists", ex.getUsername());
        return ResponseEntity.badRequest().body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(MemberDoesNotExistException.class)
    public ResponseEntity<Response> memberDoesNotExists(MemberDoesNotExistException ex) {
        log.error("Member with the memberId `{}` does not exist", ex.getMemberId());
        return ResponseEntity.badRequest().body(new Response(ex.getMessage()+ " [id: " + ex.getMemberId() + "]"));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Response> invalidRequest(InvalidRequestException ex) {
        log.error("Invalid Request- Request criteria not met: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new Response(ex.getMessage()));
    }

    @ExceptionHandler(CommitteeDoesNotExistException.class)
    public ResponseEntity<Response> committeeDoesNotExists(CommitteeDoesNotExistException ex) {
        log.error("Committee with the committeeId `{}` does not exist", ex.getCommitteeId());
        return ResponseEntity.badRequest().body(new Response(ex.getMessage()));
    }


    @ExceptionHandler(MemberNotInCommitteeException.class)
    public ResponseEntity<Response> memberNotInCommittee(MemberNotInCommitteeException ex) {
        log.error("MemberId: {} does not belong to CommitteeId: {}  ", ex.getMemberId(), ex.getCommitteeId());
        return ResponseEntity.badRequest().body(new Response(ex.getMessage()));
    }
}
