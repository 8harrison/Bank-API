package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class TransactionFixtures {


  public final static MockGen transaction_deposito = new MockGen(Map.of(
      "name", "deposito",
      "valor", "10000.00"
  ));

  public final static MockGen transaction_saque = new MockGen(Map.of(
      "name", "saque",
      "valor", "500.00"
  ));

  public final static MockGen transaction_transferencia = new MockGen(Map.of(
      "name", "transferencia",
      "valor", "500.00",
      "cpf", AccountFixtures.account_2_response.get("cpf")
  ));

  public final static MockGen transaction_pix = new MockGen(Map.of(
      "name", "PIX",
      "valor", "500.00",
      "cpf", AccountFixtures.account_2_response.get("cpf")
  ));

  public static MockGen setIdAndCode(MockGen response, MockGen request) {
    request.put("id", response.get("id"));
    request.put("code", response.get("code"));
    return request;
  }
}
