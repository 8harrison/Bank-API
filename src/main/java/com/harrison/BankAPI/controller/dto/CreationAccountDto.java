package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.utils.AccountTypes;

public record CreationAccountDto(String cpf, AccountTypes name, Double saldo, String branchCode) {


    public static Account toAccount(CreationAccountDto dto, Branch branch, Person person) {
        Account account = new Account();
        account.setSaldo(dto.saldo);
        account.setName(dto.name);
        account.setPerson(person);
        account.setBranch(branch);
        return account;
    }
}
