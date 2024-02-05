package com.harrison.BankAPI.utils;

import static com.harrison.BankAPI.mocks.MockFactory.mockPerson;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson_1;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class AccountFixtures {

  public final static MockGen account_1_response = new MockGen(Map.of(
      "name", "poupança",
      "saldo", 1000.00
  ));

  public final static MockGen account_1_request = new MockGen(Map.of(
      "cpf", mockPerson().getCpf(),
      "name", "poupança",
      "saldo", 1000.00,
      "branchCode", "0001"
  ));

  public final static MockGen account_2_response = new MockGen(Map.of(
      "saldo", 1000.00,
      "name", "corrente"
  ));

  public final static MockGen account_2_request = new MockGen(Map.of(
      "cpf", mockPerson_1().getCpf(),
      "saldo", 1000.00,
      "name", "corrente",
      "branchCode", "0001"
  ));

  public final static MockGen account_3_request = new MockGen(Map.of(
      "cpf", "777.777.777-77",
      "saldo", 1000.00,
      "name", "corrente",
      "branchCode", "0001"
  ));

  public final static MockGen account_3_response = new MockGen(Map.of(
      "saldo", 1000.00,
      "name", "corrente"
  ));
}
