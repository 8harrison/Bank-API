package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Bank;

import java.util.Map;

public class BankFixtures {

    public static Bank bankMock() {
        Bank bank = new Bank();
        bank.setIncomeTax(0.005);
        bank.setDepositTax(4.56);
        bank.setDraftTax(2.56);
        bank.setTransferTax(8.88);
        return bank;
    }

    public static final MockGen bankMock = new MockGen(Map.of(
     "name", "PoupaBank",
     "incomeTax", 0.005,
     "depositTax", 4.56,
     "draftTax", 2.56,
     "transferTax", 8.88
    ));
}
