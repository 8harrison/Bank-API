package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.mocks.MockFactory.mockPerson;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson_1;
import static com.harrison.BankAPI.mocks.MockGen.toMockGen;
import static com.harrison.BankAPI.utils.TestHelpers.getValidateToken;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static com.harrison.BankAPI.utils.TransactionFixtures.setIdAndCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.harrison.BankAPI.mocks.MockGen;
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
  aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);

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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockGen transaction = aux.performCreation(TransactionFixtures.transaction_deposito,
        "/accounts/1/transactions");

    String expected = objectToJson(transaction);
    String returnedTransaction = aux.performfind("/accounts/1/transactions/1");

    assertEquals(expected, returnedTransaction);
  }

  @Test
  public void testGetTransactionByIdNotFound() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockHttpServletRequestBuilder builder = get(
        "/accounts/1/transactions/100");
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockGen transaction = aux.performCreation(TransactionFixtures.transaction_deposito,
        "/accounts/1/transactions");

    String expected = objectToJson(transaction);
    String returnedTransaction = aux.performfind("/accounts/1/transactions?code=" + transaction.get("code"));

    assertEquals(expected, returnedTransaction);
  }

  @Test
  public void testGetTransactionByCodeNotFound() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockHttpServletRequestBuilder builder = get(
        "/accounts/1/transactions?code=0000-d");
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(toMockGen(mockPerson_1()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    aux.performCreation(AccountFixtures.account_2_request);
    MockGen transaction = aux.performCreation(TransactionFixtures.transaction_transferencia,
        "/accounts/1/transactions");

    MockHttpServletRequestBuilder builder = delete(
        "/accounts/1/transactions/" + transaction.get("id"));
    builder = builder.header("Authorization", "Bearer " + getValidateToken());
    String message = "Transação excluída com sucesso!";

    aux.performDelete(builder, message);
  }

  @Test
  public void testDeleteTransactionByIdNotFound() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockHttpServletRequestBuilder builder = delete(
        "/accounts/1/transactions/100");
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
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_deposito,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_deposito;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");
    MockGen account = toMockGen(aux.performfind("/accounts/1"));
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(11000.00, account.get("saldo"));
  }

  private void testSaque() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_saque,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_saque;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");
    MockGen account = toMockGen(aux.performfind("/accounts/1"));
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(500.00, account.get("saldo"));
  }

  private void testTransferencia() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    aux.performCreation(toMockGen(mockPerson_1()), "/auth/register");
    aux.performCreation(AccountFixtures.account_2_request);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_transferencia,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_transferencia;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen account1 = toMockGen(aux.performfind("/accounts/1"));
    MockGen account2 = toMockGen(aux.performfind("/accounts/1"));
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(500.00, account1.get("saldo"));
    assertEquals(1500.00, account2.get("saldo"));
  }

  private void testPix() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    aux.performCreation(toMockGen(mockPerson_1()), "/auth/register");
    aux.performCreation(AccountFixtures.account_2_request);
    MockGen savedtransaction = aux.performCreation(TransactionFixtures.transaction_pix,
        "/accounts/1/transactions");
    MockGen transaction = TransactionFixtures.transaction_pix;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen account1 = toMockGen(aux.performfind("/accounts/1"));
    MockGen account2 = toMockGen(aux.performfind("/accounts/1"));
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(500.00, account1.get("saldo"));
    assertEquals(1500.00, account2.get("saldo"));
  }

  private void testCreateTransactionAccountIdNotFound() throws Exception {
    aux.performCreation(toMockGen(mockPerson()), "/auth/register");
    aux.performCreation(AccountFixtures.account_1_request);
    MockHttpServletRequestBuilder builder = post(
        "/accounts/100/transactions/1");
    builder = builder.header("Authorization", "Bearer " + getValidateToken());

    String message = "Conta não encontrada!";

    aux.performNotFound(builder, message);
  }
}
