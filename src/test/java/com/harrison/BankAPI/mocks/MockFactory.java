package com.harrison.BankAPI.mocks;

import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Address;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.entity.Transaction;

public class MockFactory {

  public static Person mockPerson() {
    Person person = new Person();
    person.setName("Moacir Pitagoras");
    person.setEmail("moacir.pit@hotmail.com");
    person.setCpf("123.456.789-00");
    person.setUsername("MoacirPitagoras");
    person.setPassword("54321");
    person.setRole("CLIENT");
    return person;
  }

  public static Person mockPerson_1() {
    Person person = new Person();
    person.setName("Calice de Jesus");
    person.setEmail("calice.jesus@hotmail.com");
    person.setCpf("454.544.488-66");
    person.setUsername("caliceJesus");
    person.setPassword("123456");
    person.setRole("CLIENT");
    return person;
  }

  public static Account mockAccount() {
    Account account = new Account();
    account.setSaldo(1000.00);
    account.setName("poupança");
    return account;
  }

  public static Branch mockBranch_1() {
    Branch branch = new Branch();
    branch.setName("Agência da Praça da Matriz");
    return branch;
  }

  public static Branch mockBranch_2() {
    Branch branch = new Branch();
    branch.setName("Agência do Centro de São João de Meriti");
    return branch;
  }

  public static Branch mockBranch_3() {
    Branch branch = new Branch();
    branch.setName("Agência da Pavuna");
    return branch;
  }

  public static Address mockAddress() {
    Address address = new Address();
    address.setRua("Goias");
    address.setNumero(12);
    address.setCep("12345-666");
    return address;
  }

  public static Transaction mockTransaction_deposito() {
    Transaction transaction = new Transaction();
    transaction.setName("deposito");
    transaction.setValor(10000.00);
    return transaction;
  }

  public static Transaction mockTransaction_saque() {
    Transaction transaction = new Transaction();
    transaction.setName("saque");
    transaction.setValor(500.00);
    return transaction;
  }

  public static Transaction mockTransaction_transferencia() {
    Transaction transaction = new Transaction();
    transaction.setName("transferencia");
    transaction.setValor(500.00);
    return transaction;
  }

  public static Transaction mockTransaction_pix() {
    Transaction transaction = new Transaction();
    transaction.setName("pix");
    transaction.setValor(500.00);
    return transaction;
  }
  
}
