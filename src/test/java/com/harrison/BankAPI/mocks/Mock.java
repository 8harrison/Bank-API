package com.harrison.BankAPI.mocks;

public class Mock {

  public static Person personMock() {
    Person person = new Person();
    person.setName("Moacir Pitagoras");
    person.setCpf("12345678");
    person.setEmail("moacir.pit@hotmail.com");
    person.setUsername("MoacirPitagoras");
    person.setPassword("54321");
    person.setRole("CLIENT");
    return person;
  }

  public static Account accountMock() {
    Account account = new Account();
    account.setPerson(personMock());
    account.setSaldo(1000.00);
    return account;
  }

  public static Transaction transactionMock1() {
    Transaction transaction = new Transaction();
    transaction.setValor(1500.00);
    transaction.SetName("Deposito");
    transaction.setAccount(accountMock());
    return transaction;
  }

  public static Transaction transactionMock2() {
    Transaction transaction = new Transaction();
    transaction.setValor(500.00);
    transaction.SetName("Saque");
    transaction.setAccount(accountMock());
    return transaction;
  }
}
