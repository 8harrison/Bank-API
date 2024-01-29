package com.harrison.BankAPI.mocks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.models.entity.Address;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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

  public Account toAccount() throws NoSuchFieldException, IllegalAccessException {
    Account account = new Account();
    Class<?> fields = Account.class;
    for (String value : this.keySet()) {
      Field field = fields.getDeclaredField(value);
      field.setAccessible(true);
      field.set(account, this.get(value));
    }
    return account;
  }

  public Transaction toTransaction() {
    return objectMapper.convertValue(this, Transaction.class);
  }

  public Branch toBranch() throws IllegalAccessException, NoSuchFieldException {
    Branch branch = new Branch();
    Class<?> fields = Branch.class;
    for (String value : this.keySet()) {
      Field field = fields.getDeclaredField(value);
      field.setAccessible(true);
      field.set(branch, this.get(value));
    }
    return branch;
}

public Address toAddress() {
  return objectMapper.convertValue(this, Address.class);
}

public static MockGen toMockGen(Object object) {
  return objectMapper.convertValue(object, MockGen.class);
}

public Person toPerson()
    throws NoSuchFieldException, IllegalAccessException {
  Person person = new Person();
  Class<?> fields = Person.class;
  for (String value : this.keySet()) {
    Field field = fields.getDeclaredField(value);
    field.setAccessible(true);
    field.set(person, this.get(value));
  }
  return person;
}
}
