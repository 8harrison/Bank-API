package com.harrison.BankAPI.mocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.models.entity.Address;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MockGen extends HashMap<String, Object> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public <K, V> MockGen() {
    super();
  }

  public <K, V> MockGen(Map<K, V> source) {
    super((Map<String, Object>) source);
  }

  public MockGen clone() {
    return new MockGen(this);
  }

public static MockGen toMockGen(Object object) {
  return objectMapper.convertValue(object, MockGen.class);
}

public static MockGen toMockGen(String json) throws JsonProcessingException {
    return objectMapper.readValue(json, MockGen.class);
}

public static MockGen setIdAndCode(MockGen response, MockGen request) {
    request.put("id", response.get("id"));
    request.put("code", response.get("code"));
    request.put("address", response.get("address"));
    return request;
}

}
