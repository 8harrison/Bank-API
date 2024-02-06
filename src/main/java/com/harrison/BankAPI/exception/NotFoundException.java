package com.harrison.BankAPI.exception;

public class NotFoundException extends RuntimeException{

  public NotFoundException(String message) {
    super(message);
  }
}
