package me.safgbad.calendar.exceptions;

import org.hibernate.PropertyValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler
  public ResponseEntity<AppError> catchNoSuchElementException(NoSuchElementException e) {
    return new ResponseEntity<>(new AppError(HttpStatus.NO_CONTENT.value(), e.getMessage()),
        HttpStatus.NO_CONTENT);
  }

  @ExceptionHandler
  public ResponseEntity<AppError> catchPropertyValueException(PropertyValueException e) {
    return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<AppError> catchIllegalArgumentException(IllegalArgumentException e) {
    return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()),
        HttpStatus.BAD_REQUEST);
  }
}
