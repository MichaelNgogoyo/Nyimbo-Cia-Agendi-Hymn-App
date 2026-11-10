package org.nyimbo.agendi.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<Map<String,String>> invalid(IllegalArgumentException error) { return ResponseEntity.badRequest().body(Map.of("error", error.getMessage())); }
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException error) { return ResponseEntity.badRequest().body(Map.of("error", "Request fields did not pass validation.")); }
}
