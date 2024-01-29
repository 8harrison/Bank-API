package com.harrison.BankAPI.utils;

import static com.harrison.BankAPI.mocks.MockFactory.mockPerson;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson_1;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class AccountFixtures {

  public final static MockGen account_1_response = new MockGen(Map.of(
      "id", 1,
      "person", mockPerson().getName(),
      "cpf", mockPerson().getCpf(),
      "email", mockPerson().getEmail(),
      "name", "poupança",
      "saldo", 1000.00,
      "code", "0001-00001"
  ));

  public final static MockGen account_1_request = new MockGen(Map.of(
      "cpf", mockPerson().getCpf(),
      "name", "poupança",
      "saldo", 1000.00
  ));

  public final static MockGen account_2_response = new MockGen(Map.of(
      "id", 2,
      "person", mockPerson_1().getName(),
      "cpf", mockPerson().getCpf(),
      "email", mockPerson_1().getEmail(),
      "saldo", 1000.00,
      "name", "corrente",
      "code", "0001-00002"
  ));

  public final static MockGen account_2_request = new MockGen(Map.of(
      "cpf", mockPerson().getCpf(),
      "saldo", 1000.00,
      "name", "corrente"
  ));

  public final static MockGen account3 = new MockGen(Map.of(
      "person", PersonFixtures.person_client2,
      "saldo", 1000.00,
      "name", "poupança"
  ));
}
