package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Transaction;

public record TransactionDto(Long id, String name, Double valor,  String code) {

  public static TransactionDto toDto(Transaction transaction) {
    return new TransactionDto(
        transaction.getId(),
        transaction.getName(),
        transaction.getValor(),
        transaction.getCode()
    );
  }
}
