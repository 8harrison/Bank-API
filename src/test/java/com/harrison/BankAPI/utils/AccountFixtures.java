package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class AccountFixtures {

  public final static MockGen account1 = new MockGen(Map.of(
      "personName", PersonFixtures.person_client.get("name"),
      "cpf", PersonFixtures.person_client.get("cpf"),
      "email", PersonFixtures.person_client.get("email"),
      "saldo", 1000.00,
      "address", null,
      "name", "poupança",
      "branchId", 1
  ));

  public final static MockGen account2 = new MockGen(Map.of(
      "personName", PersonFixtures.person_client1.get("name"),
      "cpf", PersonFixtures.person_client1.get("cpf"),
      "email", PersonFixtures.person_client1.get("email"),
      "saldo", 1000.00,
      "address", null,
      "name", "corrente",
      "branchId", 2
  ));

  public final static MockGen account3 = new MockGen(Map.of(
      "personName", PersonFixtures.person_client2.get("name"),
      "cpf", PersonFixtures.person_client2.get("cpf"),
      "email", PersonFixtures.person_client2.get("email"),
      "saldo", 1000.00,
      "address", null,
      "name", "poupança",
      "branchId", 3
  ));
}
