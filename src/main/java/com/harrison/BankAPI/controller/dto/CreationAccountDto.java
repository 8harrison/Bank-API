package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.utils.AccountTypes;

public record CreationAccountDto(String cpf, AccountTypes name, Double saldo, String branchCode) {

}
