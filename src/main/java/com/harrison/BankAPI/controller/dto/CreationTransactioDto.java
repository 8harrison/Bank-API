package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Transaction;

public record CreationTransactioDto(String name, Double valor, String cpf) {

  public static Transaction toTransaction(CreationTransactioDto dto) {
    Transaction transaction = new Transaction();
    transaction.setName(dto.name);
    transaction.setValor(dto.valor);
    return transaction;
  }
}
