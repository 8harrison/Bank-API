package com.harrison.BankAPI.mocks;

import java.util.HashMap;
import java.util.Map;

public class MockGen extends HashMap<String, Object> {


  public <K, V> MockGen() {
    super();
  }

  public <K, V> MockGen(Map<K, V> source) {
    super((Map<String, Object>) source);
  }

  public MockGen clone() {
    return new MockGen(this);
  }
}
