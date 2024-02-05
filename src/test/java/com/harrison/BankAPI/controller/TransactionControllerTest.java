package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.mocks.MockGen.toMockGen;
import static com.harrison.BankAPI.utils.TestHelpers.getValidateToken;
import static com.harrison.BankAPI.utils.TestHelpers.objectMapper;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static com.harrison.BankAPI.utils.TransactionFixtures.setIdAndCode;
import static com.harrison.BankAPI.utils.TransactionFixtures.transaction_pix;
import static com.harrison.BankAPI.utils.TransactionFixtures.transaction_transferencia;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AccountFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;
import com.harrison.BankAPI.utils.TransactionFixtures;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TransactionControllerTest {

  TestHelpers aux = new TestHelpers();

  @Autowired
  WebApplicationContext wac;

  @Autowired
  MockMvc mockMvc;

  MockGen account;

  MockGen savedAccount;

  String managerToken;
  String clientToken;

  @BeforeEach
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .apply(springSecurity())
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();
    managerToken = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    perform(BranchFixtures.branch_1, post("/branches"), HttpStatus.CREATED, managerToken);
    clientToken = aux.createPersonAuthenticate(PersonFixtures.person_client);
    perform(PersonFixtures.person_client1, post("/auth/register"), HttpStatus.CREATED,
        managerToken);
    account = AccountFixtures.account_1_request;
    savedAccount = perform(account, post("/accounts"), HttpStatus.CREATED, managerToken);
    perform(AccountFixtures.account_2_request, post("/accounts"), HttpStatus.CREATED, managerToken);
  }

  @Test
  @DisplayName("Teste create transaction")
  public void testA() throws Exception {
    testDeposito();
    testSaque();
    testTransferencia();
    testPix();
    testCreateTransactionAccountIdNotFound();
  }

  @Test
  @DisplayName("Teste getAll transactions")
  public void testB() throws Exception {
    Set<MockGen> transactions = Set.of(
        TransactionFixtures.transaction_deposito,
        TransactionFixtures.transaction_saque,
        transaction_transferencia,
        transaction_pix()
    );
    List<MockGen> list = new ArrayList<>();

    for (MockGen transaction : transactions) {
      MockGen savedTransaction = perform(transaction, post("/accounts/1/transactions"),
          HttpStatus.CREATED, clientToken);
      list.add(savedTransaction);
    }
    MockGen[] response = performGetAll(get("/accounts/1/transactions"), HttpStatus.OK,
        managerToken);
    MockGen[] expected = list.toArray(new MockGen[0]);
    assertArrayEquals(expected, response);
  }

  @Test
  @DisplayName("Teste getAll transactions not found account id")
  public void testC() throws Exception {
    perform(get("/accounts/100/transactions"), HttpStatus.NOT_FOUND,
        managerToken);
  }

  @Test
  @DisplayName("Teste getTransactionById")
  public void testD() throws Exception {
    MockGen expected = perform(TransactionFixtures.transaction_deposito,
        post("/accounts/1/transactions"), HttpStatus.CREATED, clientToken);

    MockGen returnedTransaction = perform(get("/accounts/1/transactions/1"), HttpStatus.OK,
        managerToken);

    assertEquals(expected, returnedTransaction);
  }

  @Test
  @DisplayName("Teste getTransactionById not found transactionId")
  public void testE() throws Exception {
    perform(get("/accounts/1/transactions/100"), HttpStatus.NOT_FOUND, managerToken);
  }

  @Test
  @DisplayName("Teste getTransactionById not found accountId")
  public void testF() throws Exception {
    perform(get("/accounts/100/transactions/1"), HttpStatus.NOT_FOUND, managerToken);
  }

  @Test
  @DisplayName("Teste getTransactionByCode")
  public void testG() throws Exception {
    MockGen expected = perform(TransactionFixtures.transaction_deposito,
        post("/accounts/1/transactions"), HttpStatus.CREATED, clientToken);

    MockGen returnedTransaction = perform(
        get("/accounts/1/transactions/find-by-code?code=" + expected.get("code")),
        HttpStatus.OK, managerToken);

    assertEquals(expected, returnedTransaction);
  }

  @Test
  @DisplayName("Teste getTransactionByCode not found code")
  public void testH() throws Exception {
    perform(get("/accounts/1/transactions/find-by-code?code=0000-d"),
        HttpStatus.NOT_FOUND, managerToken);
  }

  @Test
  @DisplayName("Teste getTransactionByCode not found accountId")
  public void testI() throws Exception {
    perform(get("/accounts/100/transactions/find-by-code?code=0001-d"),
        HttpStatus.NOT_FOUND, managerToken);
  }

  @Test
  @DisplayName("Teste deleteTransaction")
  public void testJ() throws Exception {
    MockGen transaction = perform(TransactionFixtures.transaction_deposito,
        post("/accounts/1/transactions"), HttpStatus.CREATED, clientToken);

    perform(delete("/accounts/1/transactions/" + transaction.get("id")),
        managerToken);
  }

  @Test
  @DisplayName("Teste deleteTransaction not found transactionId")
  public void testK() throws Exception {
    perform(delete("/accounts/1/transactions/100"), HttpStatus.NOT_FOUND, managerToken);
  }

  @Test
  @DisplayName("Teste deleteTransaction not found accountId")
  public void testL() throws Exception {
    perform(delete("/accounts/100/transactions/1"), HttpStatus.NOT_FOUND, managerToken);
  }

  @Test
  @DisplayName("Teste transferencia not found cpf")
  public void testM() throws Exception {
    MockGen transfer = transaction_transferencia.clone();
    transfer.put("cpf", "111.111.111.55");
    perform(transfer, post("/accounts/1/transactions"),
        HttpStatus.NOT_FOUND, clientToken);
  }

  private void testDeposito() throws Exception {
    MockGen savedtransaction = perform(TransactionFixtures.transaction_deposito,
        post("/accounts/1/transactions"), HttpStatus.CREATED, clientToken);
    MockGen transaction = TransactionFixtures.transaction_deposito;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");
    MockGen account = perform(get("/accounts/1"), HttpStatus.OK, managerToken);
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(11000.00, account.get("saldo"));
  }

  private void testSaque() throws Exception {
    MockGen savedtransaction = perform(TransactionFixtures.transaction_saque,
        post("/accounts/1/transactions"), HttpStatus.CREATED, clientToken);
    MockGen transaction = TransactionFixtures.transaction_saque;

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");
    MockGen account = perform(get("/accounts/1"), HttpStatus.OK, managerToken);
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(10500.00, account.get("saldo"));
  }

  private void testTransferencia() throws Exception {
    MockGen transaction = transaction_transferencia.clone();
    MockGen savedtransaction = perform(transaction,
        post("/accounts/1/transactions"), HttpStatus.CREATED, clientToken);

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen account1 = perform(get("/accounts/1"), HttpStatus.OK, managerToken);
    MockGen account2 = perform(get("/accounts/2"), HttpStatus.OK, managerToken);
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);
    transaction.remove("cpf");
    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(10000.00, account1.get("saldo"));
    assertEquals(1500.00, account2.get("saldo"));
  }

  private void testPix() throws Exception {
    MockGen transaction = transaction_pix();
    MockGen savedtransaction = perform(transaction,
        post("/accounts/1/transactions"), HttpStatus.CREATED, clientToken);

    assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

    MockGen account1 = perform(get("/accounts/1"), HttpStatus.OK, managerToken);
    MockGen account2 = perform(get("/accounts/2"), HttpStatus.OK, managerToken);
    MockGen expectedTransaction = setIdAndCode(savedtransaction, transaction);
    transaction.remove("cpf");

    assertEquals(expectedTransaction, savedtransaction);
    assertEquals(9500.00, account1.get("saldo"));
    assertEquals(2000.00, account2.get("saldo"));
  }

  private void testCreateTransactionAccountIdNotFound() throws Exception {
    perform(TransactionFixtures.transaction_deposito,
        post("/accounts/100/transactions"), HttpStatus.NOT_FOUND, clientToken);
  }

  private MockGen perform(MockGen mockGen, MockHttpServletRequestBuilder builder,
      HttpStatus expectedStatus, String token)
      throws Exception {
    builder = builder.header("Authorization", "Bearer " + token);
    if (expectedStatus == HttpStatus.NOT_FOUND) {
      mockMvc.perform(builder
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectToJson(mockGen)))
          .andExpect(status().isNotFound());
      return null;
    }
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(mockGen)))
            .andExpect(status().is(expectedStatus.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    return objectMapper.readValue(responseContent, MockGen.class);
  }

  private MockGen perform(MockHttpServletRequestBuilder builder, HttpStatus expectedStatus,
      String token)
      throws Exception {
    builder = builder.header("Authorization", "Bearer " + token);
    if (expectedStatus != HttpStatus.OK) {
      mockMvc.perform(builder
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
      return null;
    }
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(expectedStatus.value()))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    return objectMapper.readValue(responseContent, MockGen.class);
  }

  private void perform(MockHttpServletRequestBuilder builder, String token)
      throws Exception {
    builder = builder.header("Authorization", "Bearer " + token);
    mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
  }

  private MockGen[] performGetAll(MockHttpServletRequestBuilder builder, HttpStatus ok,
      String token)
      throws Exception {
    builder = builder.header("Authorization", "Bearer " + token);
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    return objectMapper.readValue(responseContent, MockGen[].class);
  }
}
