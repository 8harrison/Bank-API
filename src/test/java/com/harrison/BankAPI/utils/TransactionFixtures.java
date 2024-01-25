package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class TransactionFixtures {


  public final static MockGen transaction_deposito = new MockGen(Map.of(
      "name", "deposito",
      "valor", "10000.00",
      "accounts", Map.of("contaId", AccountFixtures.account1.get("id"))
  ));

  public final static MockGen transaction_saque = new MockGen(Map.of(
      "name", "saque",
      "valor", "500.00",
      "accounts", Map.of("contaId", AccountFixtures.account1.get("id"))
  ));

  public final static MockGen transaction_transferencia = new MockGen(Map.of(
      "name", "transferencia",
      "valor", "500.00",
      "accounts", Map.of("contaOrigemId", AccountFixtures.account1.get("id"),
          "contaDestinoId", AccountFixtures.account2.get("id"))
  ));

  public final static MockGen transaction_pix = new MockGen(Map.of(
      "name", "PIX",
      "valor", "500.00",
      "accounts", Map.of("contaOrigemId", AccountFixtures.account1.get("id"),
          "contaDestinoId", AccountFixtures.account2.get("id"))
  ));
}
