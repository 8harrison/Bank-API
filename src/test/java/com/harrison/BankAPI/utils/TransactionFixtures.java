package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class TransactionFixtures {


  public final static MockGen transaction_deposito = new MockGen(Map.of(
      "name", "deposito",
      "valor", 10000.0
  ));

  public final static MockGen transaction_saque = new MockGen(Map.of(
      "name", "saque",
      "valor", 500.0
  ));

  public final static MockGen transaction_transferencia = new MockGen(Map.of(
        "name", "transferencia",
        "valor", 500.0,
        "cpf", PersonFixtures.person_client1.get("cpf")
    ));


  public final static MockGen transaction_pix() {
    return new MockGen(Map.of(
        "name", "pix",
        "valor", 500.0,
        "cpf", PersonFixtures.person_client1.get("cpf")
    ));
  }

  public static MockGen setIdAndCode(MockGen response, MockGen request) {
    request.put("id", response.get("id"));
    request.put("code", response.get("code"));
    return request;
  }
}
