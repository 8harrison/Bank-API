package com.harrison.BankAPI.exception;


public class InsulfficientFoundsException extends RuntimeException{

  public InsulfficientFoundsException(String message) {
    super(message);
  }

}
