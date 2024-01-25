package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class AddressFixtures {

  public final static MockGen client_address1 = new MockGen(Map.of(
      "rua", "Goias",
      "numero", 12,
      "cep", "12345-666"
  ));

  public final static MockGen client_address2 = new MockGen(Map.of(
      "rua", "Santos",
      "numero", 15,
      "cep", "12345-777"
  ));

  public final static MockGen client_address3 = new MockGen(Map.of(
      "rua", "Amish",
      "numero", 100,
      "cep", "12345-555"
  ));

  public final static MockGen branch_address1 = new MockGen(Map.of(
      "rua", "Turmalina",
      "numero", 25,
      "cep", "12345-222"
  ));

  public final static MockGen branch_address2 = new MockGen(Map.of(
      "rua", "Topazio",
      "numero", 18,
      "cep", "12345-111"
  ));

  public final static MockGen branch_address3 = new MockGen(Map.of(
      "rua", "Safira",
      "numero", 48,
      "cep", "12345-888"
  ));
}
