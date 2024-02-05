package com.harrison.BankAPI.exception;

public class InvalidCpfException extends RuntimeException{

  public InvalidCpfException(String message) {
    super(message);
  }
}
