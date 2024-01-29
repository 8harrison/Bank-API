package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class AccountFixtures {

  public final static MockGen account1;

  static {
    try {
      account1 = new MockGen(Map.of(
          "person", PersonFixtures.person_client.toPerson(),
          "name", "poupança",
          "saldo", 1000.00
      ));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public final static MockGen account2 = new MockGen(Map.of(
      "person", PersonFixtures.person_client1,
      "saldo", 1000.00,
      "name", "corrente"
  ));

  public final static MockGen account3 = new MockGen(Map.of(
      "person", PersonFixtures.person_client2,
      "saldo", 1000.00,
      "name", "poupança"
  ));
}
