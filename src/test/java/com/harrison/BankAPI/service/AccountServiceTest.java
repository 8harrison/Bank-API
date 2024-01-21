package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.mocks.Mock.accountMock;
import static com.harrison.BankAPI.mocks.Mock.transactionMock1;
import static com.harrison.BankAPI.mocks.Mock.transactionMock2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

  @Autowired
  private AccountService accountService;

  private Account account = accountMock();

  private Account saved = accountService.createAccount(account);

  private Transaction transaction1 = accountService.createTransaction(transactionMock1());

  private Transaction transaction2 = accountService.createTransaction(transactionMock2());

  @Test
  public void testCreateAccount() {
    assertEquals(account.getPerson(), saved.getPerson());
    assertEquals(account.getSaldo(), saved.getSaldo());
  }

  @Test
  public void testGetById() {
    Account founded = accountService.getById(saved.getId());
    assertEquals(founded.getSaldo(), saved.getSaldo());
    assertEquals(founded.getPerson(), saved.getPerson());
  }

  @Test
  public void testByGetIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.getById(100L));
  }

  @Test
  public void testGetByCode() {
    Account founded = accountService.getByCode(saved.getCode());
    assertEquals(founded.getPerson(), saved.getPerson());
    assertEquals(founded.getSaldo(), saved.getSaldo());
  }

  @Test
  public void testGetByCodeNotFound() {
    assertThrows(CodeNotFoundException.class, () ->
        accountService.getByCode("0000-00000"));
  }

  @Test
  public void testCreateTransaction() {
    assertEquals(transaction1.getName(), transactionMock1().getName());
    assertEquals(transaction1.getValor(), transactionMock1().getValor());
    assertEquals(transaction1.getAccount(), transactionMock1().getAccount());
    assertEquals(transaction2.getName(), transactionMock2().getName());
    assertEquals(transaction2.getValor(), transactionMock2().getValor());
    assertEquals(transaction2.getAccount(), transactionMock2().getAccount());
  }

  @Test
  public void testGetTransactionById() {
    Transaction founded = accountService.getTransactionById(account.getId(), transaction1.getId());

    assertEquals(founded.getName(), transaction1.getName());
    assertEquals(founded.getValor(), transaction1.getValor());
    assertEquals(founded.getCode(), transaction1.getCode());
    assertEquals(founded.getAccount(), transaction1.getAccount());
  }

  @Test
  public void testGetTransactionByIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.getTransactionById(account.getId(), 100L));
  }

  @Test
  public void testGetTransactionByCode() {
    Transaction founded = accountService.getTransactionByCode(account.getId(), transaction1.getCode());

    assertEquals(founded.getName(), transaction1.getName());
    assertEquals(founded.getValor(), transaction1.getValor());
    assertEquals(founded.getId(), transaction1.getId());
    assertEquals(founded.getAccount(), transaction1.getAccount());
  }

  @Test
  public void testGetTransactionByCodeNotFound() {
    assertThrows(CodeNotFoundException.class, () ->
        accountService.getTransactionByCode(account.getId(), "S-0000-00000"));
  }

  @Test
  public void testGetAllTransactions() {
    List<Transaction> transactionList = accountService.getAllTransactions(account.getId());

    MatcherAssert.assertThat(account.getTransactions(), CoreMatchers.is(transactionList));
  }
}
