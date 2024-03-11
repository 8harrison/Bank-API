package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.utils.TransactionTypes;

public record CreationTransactioDto(TransactionTypes name, Double valor, String cpf) {

  public static Transaction toTransaction(CreationTransactioDto dto) {
    Transaction transaction = new Transaction();
    transaction.setName(dto.name);
    transaction.setValor(dto.valor);
    return transaction;
  }
}
