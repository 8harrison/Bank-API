package com.harrison.BankAPI.utils;

import com.harrison.BankAPI.mocks.MockGen;
import java.util.ArrayList;
import java.util.Map;

public class BranchFixtures {

  public final static MockGen branch_1 = new MockGen(Map.of(
      "name", "Agência da Praça da Matriz"
  ));

  public final static MockGen branch_2 = new MockGen(Map.of(
      "name", "Agência do Centro de São João de Meriti"
  ));

  public final static MockGen branch_3 = new MockGen(Map.of(
      "name", "Agência da Pavuna"
  ));
}
