package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

import static com.harrison.BankAPI.mocks.MockFactory.mockPerson_1;

public class TransactionFixtures {


  public final static MockGen transaction_deposito = new MockGen(Map.of(
      "name", "DEPOSITO",
      "valor", 10000.0
  ));

  public final static MockGen transaction_saque = new MockGen(Map.of(
      "name", "SAQUE",
      "valor", 500.0
  ));

  public final static MockGen transaction_transferencia = new MockGen(Map.of(
        "name", "DOC",
        "valor", 500.0,
        "cpf", mockPerson_1().getCpf()
    ));


  public static MockGen transaction_pix() {
    return new MockGen(Map.of(
        "name", "PIX",
        "valor", 500.0,
        "cpf", mockPerson_1().getCpf()
    ));
  }
}
