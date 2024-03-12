package com.harrison.BankAPI.exception;

import com.harrison.BankAPI.utils.SecurityFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GerenciadorExceptionController {

  @ExceptionHandler({NotFoundException.class})
  public ResponseEntity<String> handleNotFound(RuntimeException exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(exception.getMessage());
  }

  @ExceptionHandler({InsulfficientFoundsException.class,
      InvalidCpfException.class, InvalidEmailException.class, ConflictUsernameException.class})
  public ResponseEntity<String> handleBadRequest(RuntimeException exception) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(exception.getMessage());
  }

  @ExceptionHandler({InvalidAcessException.class})
  public ResponseEntity<String> handleInvalidAccessException(RuntimeException exception) {
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(exception.getMessage());
  }
}
