package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.Map;

public class BranchFixtures {

  private static final MockGen[] gerentes = new MockGen[]{PersonFixtures.person_admin,
      PersonFixtures.person_admin_1};

  public final static MockGen branch_1 = new MockGen(Map.of(
      "name", "Agência da Praça da Matriz",
      "address", null,
      "code", "0001",
      "gerentes", gerentes,
      "diretor", PersonFixtures.person_manager
  ));

  public final static MockGen branch_2 = new MockGen(Map.of(
      "name", "Agência do Centro de São João de Meriti",
      "address", null,
      "code", "0002",
      "gerentes", new MockGen[]{PersonFixtures.person_admin_2},
      "diretor", PersonFixtures.person_manager
  ));

  public final static MockGen branch_3 = new MockGen(Map.of(
      "name", "Agência da Pavuna",
      "address", null,
      "code", "0003",
      "gerentes", new MockGen[]{},
      "diretor", PersonFixtures.person_manager
  ));
}
