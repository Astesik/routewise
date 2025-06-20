// src/main/java/com/example/ioproject/exceptions/GlobalExceptionHandler.java

package com.example.ioproject.common.exception;

import com.example.ioproject.auth.exception.RoleNotFoundException;
import com.example.ioproject.common.exception.ErrorResponse;
import com.example.ioproject.auth.dto.response.MessageResponse;
import com.example.ioproject.driver.exception.DriverNotFoundException;
import com.example.ioproject.place.exception.PlaceNotFoundException;
import com.example.ioproject.repair.exception.RepairNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<MessageResponse> handleRoleNotFound(RoleNotFoundException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(PlaceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePlaceNotFound(PlaceNotFoundException ex) {
        return ResponseEntity.badRequest().body(new ErrorResponse(ex.getMessage(), 400));
    }

    @ExceptionHandler(RepairNotFoundException.class)
    public ResponseEntity<?> handleRepairNotFound(RepairNotFoundException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<?> handleDriverNotFound(DriverNotFoundException ex) {
        return ResponseEntity.badRequest().body(new MessageResponse(ex.getMessage()));
    }

}
