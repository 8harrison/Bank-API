package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.utils.TransactionTypes;

import java.time.LocalDate;

public record TransactionDto(Long id, TransactionTypes name, Double valor, String code, LocalDate createdDate) {

    public static TransactionDto toDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getName(),
                transaction.getValor(),
                transaction.getCode(),
                transaction.getCreatedDate()
        );
    }
}
