package com.harrison.BankAPI.mocks;

import com.harrison.BankAPI.models.entity.Person;

public class Mock {

  public static Person personMock1() {
    Person person = new Person();
    person.setName("Moacir Pitagoras");
    person.setCpf("12345678");
    person.setEmail("moacir.pit@hotmail.com");
    person.setUsername("MoacirPitagoras");
    person.setPassword("54321");
    person.setRole("CLIENT");
    return person;
  }

  public static Person personMock2() {
    Person person = new Person();
    person.setName("Calice de Jesus");
    person.setCpf("45454448866");
    person.setEmail("calice.jesus@hotmail.com");
    person.setUsername("calicejesus");
    person.setPassword("123456");
    person.setRole("CLIENT");
    return person;
  }

  public static Person personMock3() {
    Person person = new Person();
    person.setName("Juraci Candongas");
    person.setCpf("77777777777");
    person.setEmail("juraci.candangas@hotmail.com");
    person.setUsername("juracicandongas");
    person.setPassword("777777");
    person.setRole("CLIENT");
    return person;
  }

  public static Person personAdminMock() {
    Person person = new Person();
    person.setName("Janio Quadros");
    person.setCpf("65478123");
    person.setEmail("janio.quadros@hotmail.com");
    person.setUsername("janioQua");
    person.setPassword("789654");
    person.setRole("ADMIN");
    return person;
  }

  public static Account accountMock1() {
    Account account = new Account();
    account.setPerson(personMock1());
    account.setSaldo(1000.00);
    return account;
  }

  public static Account accountMock2() {
    Account account = new Account();
    account.setPerson(personMock2());
    account.setSaldo(1000.00);
    return account;
  }

  public static Transaction transactionMock1() {
    Transaction transaction = new Transaction();
    transaction.setValor(1500.00);
    transaction.SetName("Deposito");
    transaction.getAccounts().put("conta", accountMock1());
    return transaction;
  }

  public static Transaction transactionMock2() {
    Transaction transaction = new Transaction();
    transaction.setValor(500.00);
    transaction.SetName("Saque");
    transaction.getAccounts().put("conta", accountMock1());
    return transaction;
  }

  public static Transaction transactionMock3() {
    Transaction transaction = new Transaction();
    transaction.setValor(500.00);
    transaction.SetName("Transferência");
    transaction.getAccounts().put("contaDe", accountMock1());
    transaction.getAccounts().put("contaPara", accountMock2());
    return transaction;
  }

  public static Transaction transactionMock4() {
    Transaction transaction = new Transaction();
    transaction.setValor(500.00);
    transaction.SetName("PIX");
    transaction.getAccounts().put("contaDe", accountMock1());
    transaction.getAccounts().put("contaPara", accountMock2());
    return transaction;
  }
}
