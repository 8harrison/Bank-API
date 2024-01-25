package com.harrison.BankAPI.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AccountFixtures;
import com.harrison.BankAPI.utils.TransactionFixtures;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionServiceTest {

  @Autowired
  private AccountService accountService;

  private final Account saved1 = accountService.createAccount(AccountFixtures.account1);

  private final Account saved2 = accountService.createAccount(AccountFixtures.account2);

  @Test
  public void testCreateTransaction() {
    testDeposito();
    testSaque();
    testTranseferencia();
    testPix();
    testSaqueInsulfficientFounds();
    testTransferenciaAccountIdNotFound();
    testTransferenciaInsulfficientFounds();
    testPixInsulfficientFounds();

  }

  @Test
  public void testGetTransactionById() {
    Transaction transaction = accountService.createTransaction(1L,
        TransactionFixtures.transaction_deposito);
    Transaction founded = accountService.getTransactionById(1L, transaction.getId());

    MockGen expected = new MockGen(transaction);
    MockGen response = new MockGen(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetTransactionByIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.getTransactionById(saved1.getId(), 100L));
  }

  @Test
  public void testGetTransactionByIdAccountIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.getTransactionById(100L, 1L));
  }

  @Test
  public void testGetTransactionByCode() {
    Transaction transaction = accountService.createTransaction(1L,
        TransactionFixtures.transaction_deposito);
    TRansaction founded = accountService.getTRansactionByCode(1L, transaction.getCode());

    MockGen expected = new MockGen(transaction);
    MockGen response = new MockGen(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetTransactionByCodeNotFound() {
    assertThrows(CodeNotFoundException.class, () ->
        accountService.getTransactionByCode(saved1.getId(), "0000-s"));
  }

  @Test
  public void testGetTransactionByCodeAccountIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.getTransactionByCode(100L, "0001-s"));
  }

  @Test
  public void testGetAllTransactions() {
    List<Transaction> transactionList = accountService.getAllTransactions(saved1.getId());

    List<MockGen> response = new ArrayList<>();
    for (Transaction transaction : transactionList) {
      response.add(new MockGen(transaction));
    }

    List<MockGen> expected = new ArrayList<>();
    for (Transaction transaction : saved1.getTransactions()) {
      expected.add(new MockGen(transaction));
    }

    assertEquals(expected, response);
  }

  @Test
  public void testGetAllTransactionsAccountIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.getAllTransactions(100L));
  }

  @Test
  public void testeDeleteTransaction() {
    Transaction transaction = accountService.createTransaction(1L,
        TransactionFixtures.transaction_deposito);

    String message = accountService.deleteTransaction(1L, transaction.getId());

    assertEquals("Transação excluída com sucesso!", message);
    assertThrows(IdNotFoundException.class, () ->
        accountService.getTransactionById(1L, transaction.getId()));
  }

  @Test
  public void testDeleteTransactionIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.deleteTransaction(1L, 100L));
  }

  @Test
  public void testDeleteTransactionAccountIdNotFound() {
    assertThrows(IdNotFoundException.class, () ->
        accountService.deleteTransaction(100L, 1L));
  }

  private void testDeposito() {
    MockGen expected = TransactionFixtures.transaction_deposito;
    Transaction created = accountService.createTransaction(1L, expected);

    expected.put("id", created.getId());
    expected.put("code", created.getCode());

    MockGen response = new MockGen(created);

    assertEquals(expected, response);
    assertEquals(11000.00, saved1.getSaldo());

  }

  private void testSaque() {
    MockGen expected = TransactionFixtures.transaction_saque;
    Transaction created = accountService.createTransaction(1L, expected);

    expected.put("id", created.getId());
    expected.put("code", created.getCode());

    MockGen response = new MockGen(created);

    assertEquals(expected, response);
    assertEquals(10500.00, saved1.getSaldo());
  }

  private void testSaqueInsulfficientFounds() {
    MockGen expected = TransactionFixtures.transaction_saque;
    expected.put("valor", 15000.00);

    assertThrows(InsulfficientFoundsException.class, () ->
        accountService.createTransaction(1L, expected));

  }

  private void testTransferenciaAccountIdNotFound() {
    MockGen expected = TransactionFixtures.transaction_saque;

    assertThrows(IdNotFoundException.class, () ->
        accountService.createTransaction(100L, expected));

  }

  private void testTranseferencia() {
    MockGen expected = TransactionFixtures.transaction_transferencia;
    Transaction transaction = accountService.createTransaction(1L, expected);

    expected.put("id", transaction.getId());
    expected.put("code", transaction.getCode());

    MockGen response = new MockGen(transaction);

    assertEquals(expected, response);
    assertEquals(10000.00, saved1.getSaldo());
    assertEquals(1500.00, saved2.getSaldo());

  }

  private void testTransferenciaInsulfficientFounds() {
    MockGen expected = TransactionFixtures.transaction_transferencia;
    expected.put("valor", 15000.00);

    assertThrows(InsulfficientFoundsException.class, () ->
        accountService.createTransaction(1L, expected));

  }

  private void testPix() {
    MockGen expected = TransactionFixtures.transaction_pix;
    Transaction transaction = accountService.createTransaction(1L, expected);

    expected.put("id", transaction.getId());
    expected.put("code", transaction.getCode());

    MockGen response = new MockGen(transaction);

    assertEquals(expected, response);
    assertEquals(9500.00, saved1.getSaldo());
    assertEquals(2000.00, saved2.getSaldo());
  }

  private void testPixInsulfficientFounds() {
    MockGen expected = TransactionFixtures.transaction_pix;
    expected.put("valor", 15000.00);

    assertThrows(InsulfficientFoundsException.class, () ->
        accountService.createTransaction(1L, expected));

  }
}
