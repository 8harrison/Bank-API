package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.TestHelpers.getValidateToken;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.service.AccountService;
import com.harrison.BankAPI.utils.AccountFixtures;
import com.harrison.BankAPI.utils.TestHelpers;
import com.harrison.BankAPI.utils.TransactionFixtures;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionControllerTest {

  @Autowired
  AccountService accountService;

  TestHelpers aux = new TestHelpers();

  @Test
  public void testCreateTransaction() throws Exception {
    testDeposito();
    testSaque();
    testTransferencia();
    testPix();
    testCreateTransactionAccountIdNotFound();
  }

  @Test
  public void testgetAllTransactions() throws Exception {
    aux.performCreation(AccountFixtures.account1);

    Set<MockGen> transactions = Set.of(
        TransactionFixtures.transaction_deposito,
        TransactionFixtures.transaction_saque,
        TransactionFixtures.transaction_transferencia,
        TransactionFixtures.transaction_pix
    );

    Set<MockGen> expectedTransactions = new HashSet<>();

    for (MockGen transaction : transactions) {
      MockGen savedTransaction = aux.performCreation(transaction, "/accounts/1/transactions");
      expectedTransactions.add(savedTransaction);
    }

    String expected = objectToJson(expectedTransactions);
    String returnedTransactions = aux.performfind("/accounts/1/transactions");

    assertEquals(expected, returnedTransactions);

  }

  @Test
  public void testGetAllTransactionsAccountIdNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = get(
        "/accounts/100/transactions");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testGetTransactionById() throws Exception {
    aux.performCreation(AccountFixtures.account1);
    MockGen transaction = aux.performCreation(TransactionFixtures.transaction_deposito,
        "/accounts/1/transactions");

    String expected = objectToJson(transaction);
    String returnedTransaction = aux.performfind("/accounts/1/transactions/1");

    assertEquals(expected, returnedTransaction);
  }

  @Test
  public void testGetTransactionByIdNotFound() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockHttpServletRequestBuilder builder = get(
        "/accounts/" + account.get("id") + "/transactions/100");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Transação não encontrada";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testGetTransactionByIdAccountIdNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = get(
        "/accounts/100/transactions/1");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testGetTransactionByCode() throws Exception {
    aux.performCreation(AccountFixtures.account1);
    MockGen transaction = aux.performCreation(TransactionFixtures.transaction_deposito,
        "/accounts/1/transactions");

    String expected = objectToJson(transaction);
    String returnedTransaction = aux.performfind("/accounts/1/transactions?code=" + transaction.get("code"));

    assertEquals(expected, returnedTransaction);
  }

  @Test
  public void testGetTransactionByCodeNotFound() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockHttpServletRequestBuilder builder = get(
        "/accounts/" + account.get("id") + "/transactions?code=0000-d");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Transação não encontrada";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testGetTransactionByCodeAccountIdNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = get(
        "/accounts/100/transactions?code=0001-d");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testDeleteTransaction() throws Exception {
    MockGen account1 = aux.performCreation(AccountFixtures.account1);
    MockGen transaction = aux.performCreation(TransactionFixtures.transaction_transferencia,
        "/accounts/1/transactions");
    Object destinatarioId = transaction.get("contaDestinoId");
    Account account2 = accountService.getById((Long) destinatarioId);

    MockHttpServletRequestBuilder builder = delete(
        "/accounts/" + destinatarioId + "/transactions/" + transaction.get("id"));
    builder = builder.header("Authorization", "Bearer " + getValidateToken());
    String message = "Transação excluída com sucesso!";

    aux.performDelete(builder, message);

    assertEquals(1000.00, account1.get("saldo"));
    assertEquals(1000.00, account2.getSaldo());
  }

  @Test
  public void testDeleteTransactionByIdNotFound() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockHttpServletRequestBuilder builder = delete(
        "/accounts/" + account.get("id") + "/transactions/100");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Transação não encontrada!";

    aux.performNotFound(builder, message);

  }

  @Test
  public void testDeleteTransactionsAccountIdNotFound() throws Exception {
    MockHttpServletRequestBuilder builder = delete(
        "/accounts/100/transactions/1");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }

  private void testDeposito() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_deposito,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_deposito;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen expectedTransaction = new MockGen(transaction);
    expectedTransaction.put("id", savedtransaction.get("id"));

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(11000.00, account.get("saldo"));
  }

  private void testSaque() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_saque,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_saque;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen expectedTransaction = new MockGen(transaction);
    expectedTransaction.put("id", savedtransaction.get("id"));

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(500.00, account.get("saldo"));
  }

  private void testTransferencia() throws Exception {
    MockGen account1 = aux.performCreation(AccountFixtures.account1);
    MockGen account2 = aux.performCreation(AccountFixtures.account2);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_transferencia,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_transferencia;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen expectedTransaction = new MockGen(transaction);
    expectedTransaction.put("id", savedtransaction.get("id"));

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(500.00, account1.get("saldo"));
    assertEquals(1500.00, account2.get("saldo"));
  }

  private void testPix() throws Exception {
    MockGen account1 = aux.performCreation(AccountFixtures.account1);
    MockGen account2 = aux.performCreation(AccountFixtures.account2);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_pix,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_pix;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen expectedTransaction = new MockGen(transaction);
    expectedTransaction.put("id", savedtransaction.get("id"));

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(500.00, account1.get("saldo"));
    assertEquals(1500.00, account2.get("saldo"));
  }

  private void testCreateTransactionAccountIdNotFound() throws Exception {
    MockGen account = aux.performCreation(AccountFixtures.account1);
    MockHttpServletRequestBuilder builder = post(
        "/accounts/100/transactions/1");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);

  }
}
