package com.harrison.BankAPI.service;

import static com.harrison.BankAPI.mocks.MockFactory.mockAccount;
import static com.harrison.BankAPI.mocks.MockFactory.mockBranch_1;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson;
import static com.harrison.BankAPI.mocks.MockFactory.mockPerson_1;
import static com.harrison.BankAPI.mocks.MockFactory.mockTransaction_deposito;
import static com.harrison.BankAPI.mocks.MockFactory.mockTransaction_pix;
import static com.harrison.BankAPI.mocks.MockFactory.mockTransaction_saque;
import static com.harrison.BankAPI.mocks.MockFactory.mockTransaction_transferencia;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.harrison.BankAPI.exception.InsulfficientFoundsException;
import com.harrison.BankAPI.exception.NotFoundException;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.models.entity.Branch;
import com.harrison.BankAPI.models.entity.Person;
import com.harrison.BankAPI.models.entity.Transaction;
import java.util.ArrayList;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.annotation.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class TransactionServiceTest {

  @Autowired
  AccountService accountService;

  @Autowired
  BranchService branchService;

  @Autowired
  PersonService personService;

  Account account1;

  Account account2;

  Transaction transaction;

  @BeforeEach
  public void setup() {
    Person person1 = personService.register(mockPerson());
    Person person2 = personService.register(mockPerson_1());
    account2 = mockAccount();
    account1 = mockAccount();
    Branch branch = branchService.create(mockBranch_1());
    account1.setPerson(person1);
    account2.setPerson(person2);
    account1 = accountService.createAccount(account1, branch.getCode());
    account2 = accountService.createAccount(account2, branch.getCode());
    transaction = mockTransaction_deposito();
    transaction.setTitular(account1);
    transaction = accountService.createTransaction(account1.getId(), transaction);
  }

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
    Transaction founded = accountService.getTransactionById(account1.getId(), transaction.getId());

    String expected = objectToJson(transaction);
    String response = objectToJson(founded);
    assertEquals(expected, response);
  }

  @Test
  public void testGetTransactionByIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.getTransactionById(account1.getId(), 100L));
  }

  @Test
  public void testGetTransactionByIdAccountIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.getTransactionById(100L, 1L));
  }

  @Test
  public void testGetTransactionByCode() {
    Transaction founded = accountService.getTransactionByCode(1L, transaction.getCode());
    String expected = objectToJson(transaction);
    String response = objectToJson(founded);

    assertEquals(expected, response);
  }

  @Test
  public void testGetTransactionByCodeNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.getTransactionByCode(account1.getId(), "0000-s"));
  }

  @Test
  public void testGetTransactionByCodeAccountIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.getTransactionByCode(100L, "0001-s"));
  }

  @Test
  public void testGetAllTransactions() {
    List<Transaction> transactionList = accountService.getAllTransactions(account1.getId());
    List<String> response = new ArrayList<>();
    for (Transaction transaction1 : transactionList) {
      response.add(objectToJson(transaction1));
    }
    List<String> expected = new ArrayList<>();
    for (Transaction transaction1 : account1.getTransactions()) {
      expected.add(objectToJson(transaction1));
    }
    assertEquals(expected, response);
  }

  @Test
  public void testGetAllTransactionsAccountIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.getAllTransactions(100L));
  }

  @Test
  public void testeDeleteTransaction() {
    String message = accountService.deleteTransaction(1L, transaction.getId());

    assertEquals("Transação excluída com sucesso!", message);
  }

  @Test
  public void testDeleteTransactionIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.deleteTransaction(1L, 100L));
  }

  @Test
  public void testDeleteTransactionAccountIdNotFound() {
    assertThrows(NotFoundException.class, () ->
        accountService.deleteTransaction(100L, 1L));
  }

  private void testDeposito() {
    Transaction deposito = mockTransaction_deposito();
    deposito.setTitular(account1);
    Transaction created = accountService.createTransaction(1L, deposito);

    deposito.setId(created.getId());
    deposito.setCode(created.getCode());
    String expected = objectToJson(deposito);
    String response = objectToJson(created);

    assertEquals(expected, response);
    assertEquals(21000.00, account1.getSaldo());

  }

  private void testSaque() {
    Transaction saque = mockTransaction_saque();
    saque.setTitular(account1);
    Transaction created = accountService.createTransaction(1L, saque);

    saque.setId(created.getId());
    saque.setCode(created.getCode());
    String expected = objectToJson(saque);
    String response = objectToJson(created);

    assertEquals(expected, response);
    assertEquals(20500.00, account1.getSaldo());
  }

  private void testSaqueInsulfficientFounds() {
    Transaction saque = mockTransaction_saque();
    saque.setTitular(account1);
    saque.setValor(25000.00);

    assertThrows(InsulfficientFoundsException.class, () ->
        accountService.createTransaction(account1.getId(), saque));

  }

  private void testTransferenciaAccountIdNotFound() {
    Transaction saque = mockTransaction_saque();

    assertThrows(NotFoundException.class, () ->
        accountService.createTransaction(100L, saque));

  }

  private void testTranseferencia() {
    Transaction transferencia = mockTransaction_transferencia();
    transferencia.setTitular(account1);
    transferencia.setRecebedor(account2);
    Transaction transaction = accountService.createTransaction(1L, transferencia);

    transferencia.setId(transaction.getId());
    transferencia.setCode(transaction.getCode());
    String expected = objectToJson(transferencia);
    String response = objectToJson(transaction);

    assertEquals(expected, response);
    assertEquals(20000.00, account1.getSaldo());
    assertEquals(1500.00, account2.getSaldo());

  }

  private void testTransferenciaInsulfficientFounds() {
   Transaction expected = mockTransaction_transferencia();
   expected.setTitular(account1);
   expected.setRecebedor(account2);
    expected.setValor(25000.00);

    assertThrows(InsulfficientFoundsException.class, () ->
        accountService.createTransaction(1L, expected));

  }

  private void testPix() {
    Transaction pix = mockTransaction_pix();
    pix.setTitular(account1);
    pix.setRecebedor(account2);
    Transaction transaction = accountService.createTransaction(1L, pix);

    pix.setId(transaction.getId());
    pix.setCode(transaction.getCode());
    String expected = objectToJson(pix);
    String response = objectToJson(transaction);

    assertEquals(expected, response);
    assertEquals(19500.00, account1.getSaldo());
    assertEquals(2000.00, account2.getSaldo());
  }

  private void testPixInsulfficientFounds() {
    Transaction expected = mockTransaction_pix();
    expected.setTitular(account1);
    expected.setRecebedor(account2);
    expected.setValor(25000.00);

    assertThrows(InsulfficientFoundsException.class, () ->
        accountService.createTransaction(1L, expected));

  }
}
