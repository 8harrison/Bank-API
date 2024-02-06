package com.harrison.BankAPI.exception;

public class ConflictUsernameException extends RuntimeException{

  public ConflictUsernameException(String message) {
    super(message);
  }
}
