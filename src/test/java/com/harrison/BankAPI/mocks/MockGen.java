package com.harrison.BankAPI.mocks;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.utils.Address;
import java.util.HashMap;
import java.util.Map;

public class MockGen extends HashMap<String, Object> {

  private static ObjectMapper objectMapper;

  public <K, V> MockGen() {
    super();
  }

  public <K, V> MockGen(Map<K, V> source) {
    super((Map<String, Object>) source);
  }

  public MockGen clone() {
    return new MockGen(this);
  }

  public Person toPerson() throws JsonProcessingException {
    String json = objectToJson(this);
    return objectMapper.readValue(json, Person.class);
  }

  public Account toAccount() throws JsonProcessingException {
    String json = objectToJson(this);
    return objectMapper.readValue(json, Account.class);
  }

  public Transaction toTransaction() throws JsonProcessingException {
    String json = objectToJson(this);
    return objectMapper.readValue(json, Transaction.class);
  }

  public Branch toBranch() throws JsonProcessingException {
    String json = objectToJson(this);
    return objectMapper.readValue(json, Branch.class);
  }

  public Address toAddress() throws JsonProcessingException {
    String json = objectToJson(this);
    return objectMapper.readValue(json, Address.class);
  }

  public static MockGen toMockGen(String json) throws JsonProcessingException {
    return objectMapper.readValue(json, MockGen.class);
  }
}
