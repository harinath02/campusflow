package com.campusflow.exception;

import com.campusflow.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAlreadyExists(
            ResourceAlreadyExistsException ex) {

        return new ResponseEntity<>(
                ApiResponse.<Object>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .data(null)
                        .build(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
            ResourceNotFoundException ex) {

        return new ResponseEntity<>(
                ApiResponse.<Object>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .data(null)
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                ApiResponse.<Object>builder()
                        .status("error")
                        .message(message.isBlank() ? "Validation failed" : message)
                        .data(null)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex) {

        return new ResponseEntity<>(
                ApiResponse.<Object>builder()
                        .status("error")
                        .message(ex.getMessage() == null ? "Bad request" : ex.getMessage())
                        .data(null)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequest(
            BadRequestException ex) {

        return new ResponseEntity<>(
                ApiResponse.<Object>builder()
                        .status("error")
                        .message(ex.getMessage())
                        .data(null)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex) {

        return new ResponseEntity<>(
                ApiResponse.<Object>builder()
                        .status("error")
                        .message("Something went wrong")
                        .data(null)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
