package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;

public record AccountDto(Long id, String name, Double saldo, String code, Address address) {

  public static AccountDto toDto(Account account) {
    return new AccountDto(
        account.getId(),
        account.getName(),
        account.getSaldo(),
        account.getCode(),
        account.getAddress()
    );
  }

  public static Account toAccount(AccountDto dto, Branch branch) {
    Account account = new Account();
    account.setSaldo(dto.saldo);
    account.setId(dto.id);
    account.setCode(dto.code);
    account.setName(dto.name);
    account.setBranch(branch);
    return account;
  }
}