package com.harrison.BankAPI.controller.dto;

import com.harrison.BankAPI.models.entity.Bank;

public record UpdateBankDto(Double incomeTax, Double depositTax, Double draftTax, Double transferTax) {

    public Bank toBank(Bank bank) {
        bank.setTransferTax(this.transferTax);
        bank.setDepositTax(this.depositTax);
        bank.setIncomeTax(this.incomeTax);
        bank.setDraftTax(this.draftTax);
        return bank;
    }
}
