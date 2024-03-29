package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.controller.dto.CreationTransactioDto.toTransaction;
import static com.harrison.BankAPI.mocks.MockFactory.*;
import static com.harrison.BankAPI.mocks.MockGen.stringToDto;
import static com.harrison.BankAPI.mocks.MockGen.toMockGen;
import static com.harrison.BankAPI.utils.AccountFixtures.*;
import static com.harrison.BankAPI.utils.BranchFixtures.*;
import static com.harrison.BankAPI.utils.PersonFixtures.*;
import static com.harrison.BankAPI.utils.TestHelpers.objectMapper;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static com.harrison.BankAPI.utils.TransactionFixtures.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.harrison.BankAPI.controller.dto.CreationTransactioDto;
import com.harrison.BankAPI.controller.dto.TransactionDto;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Transaction;
import com.harrison.BankAPI.service.AccountService;
import com.harrison.BankAPI.utils.BankFixtures;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    MockGen account2;

    String managerToken;
    String clientToken;

    @Autowired
    AccountService accountService;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .alwaysDo(new SimpleResultHandler())
                .build();
        managerToken = aux.createPersonAuthenticate(person_manager);
        perform(BankFixtures.bankMock, post("/bank"), managerToken);
        perform(branch_1, post("/branches"), managerToken);
        clientToken = aux.createPersonAuthenticate(PersonFixtures.person_client);
        perform(person_client1, post("/auth/register"),
                managerToken);
        account = account_1_request;
        savedAccount = perform(account, post("/accounts"), managerToken);
        account2 = perform(account_2_request, post("/accounts"), managerToken);
    }

    @Test
    @DisplayName("Teste create transaction")
    public void testA() throws Exception {
        testDeposito();
        testCreateTransactionAccountIdNotFound();
        testInsulfficientFoundsenException();
    }

    @Test
    @DisplayName("Teste getAll transactions")
    public void testB() throws Exception {
        List<MockGen> list = saveTransactions();
        MockGen[] response = performGetAll(get("/accounts/1/transactions"),
                managerToken);
        MockGen[] expected = list.toArray(new MockGen[0]);
        assertArrayEquals(expected, response);
    }

    @Test
    public void testGetTransactionsByPeriod() throws Exception {
        List<MockGen> list = saveTransactions();
        LocalDate start = stringToDate("2024-01-15");
        LocalDate end = stringToDate("2024-01-25");
        List<Transaction> trans = changeCreatedDate();
        List<TransactionDto> dtos = trans.stream()
                .map(TransactionDto::toDto).toList();
        MockGen[] response = performGetAll(get("/accounts/1/transactions/period?start=" + start + "&end=" + end), managerToken);
        MockGen[] mockGens = dtos.stream()
                        .map(MockGen::toMockGen).toList().toArray(new MockGen[0]);
        assertEquals(objectToJson(mockGens), objectToJson(response));
    }

    private List<Transaction> changeCreatedDate() {
        Transaction deposito = mockTransaction_deposito();
        Transaction saque = mockTransaction_saque();
        Transaction transferencia = mockTransaction_transferencia();
        Transaction pix = mockTransaction_pix();
        List<Transaction> entrada = List.of(deposito, saque, transferencia, pix);
        List<Transaction> list = saveTransaction(entrada);
        deposito.setCreatedDate(stringToDate("2024-01-15"));
        saque.setCreatedDate(stringToDate("2024-01-20"));
        transferencia.setCreatedDate(stringToDate("2024-01-25"));
        pix.setCreatedDate(stringToDate("2024-02-16"));
        List<Transaction> l = new ArrayList<>();
        List<Transaction> newList = saveTransaction(list);
        l.add(newList.get(0));
        l.add(newList.get(1));
        l.add(newList.get(2));
        return l;
    }

    private List<Transaction> saveTransaction(List<Transaction> trans) {
        List<Transaction> list = new ArrayList<>();
        list.add(accountService.createTransaction(1L, trans.get(0), null));
        list.add(accountService.createTransaction(1L, trans.get(1), null));
        list.add(accountService.createTransaction(1L, trans.get(2), account2.get("cpf").toString()));
        list.add(accountService.createTransaction(1L, trans.get(3), account2.get("cpf").toString()));
        return list;
    }

    private List<MockGen> saveTransactions() throws Exception {
        List<MockGen> transactions = List.of(
                transaction_deposito,
                transaction_saque,
                transaction_transferencia,
                transaction_pix()
        );
        List<MockGen> list = new ArrayList<>();

        for (MockGen transaction : transactions) {
            MockGen savedTransaction = perform(transaction, post("/accounts/" + savedAccount.get("id") + "/transactions"),
                    clientToken);
            list.add(savedTransaction);
        }
        return list;
    }

    @Test
    @DisplayName("Teste getAll transactions not found account id")
    public void testC() throws Exception {
        perform(get("/accounts/100/transactions"), NOT_FOUND,
                managerToken);
    }

    @Test
    @DisplayName("Teste getTransactionById")
    public void testD() throws Exception {
        MockGen expected = perform(transaction_deposito,
                post("/accounts/1/transactions"), clientToken);

        MockGen returnedTransaction = perform(get("/accounts/1/transactions/1"), OK,
                managerToken);

        assertEquals(expected, returnedTransaction);
    }

    @Test
    @DisplayName("Teste getTransactionById not found transactionId")
    public void testE() throws Exception {
        perform(get("/accounts/1/transactions/100"), NOT_FOUND, managerToken);
    }

    @Test
    @DisplayName("Teste getTransactionById not found accountId")
    public void testF() throws Exception {
        perform(get("/accounts/100/transactions/1"), NOT_FOUND, managerToken);
    }

    @Test
    @DisplayName("Teste getTransactionByCode")
    public void testG() throws Exception {
        MockGen expected = perform(transaction_deposito,
                post("/accounts/1/transactions"), clientToken);

        MockGen returnedTransaction = perform(
                get("/accounts/1/transactions/find-by-code?code=" + expected.get("code")),
                OK, managerToken);

        assertEquals(expected, returnedTransaction);
    }

    @Test
    @DisplayName("Teste getTransactionByCode not found code")
    public void testH() throws Exception {
        perform(get("/accounts/1/transactions/find-by-code?code=0000-d"),
                NOT_FOUND, managerToken);
    }

    @Test
    @DisplayName("Teste getTransactionByCode not found accountId")
    public void testI() throws Exception {
        perform(get("/accounts/100/transactions/find-by-code?code=0001-d"),
                NOT_FOUND, managerToken);
    }

    @Test
    @DisplayName("Teste deleteTransaction")
    public void testJ() throws Exception {
        MockGen transaction = perform(transaction_deposito,
                post("/accounts/1/transactions"), clientToken);

        perform(delete("/accounts/1/transactions/" + transaction.get("id")),
                managerToken);
    }

    @Test
    @DisplayName("Teste deleteTransaction not found transactionId")
    public void testK() throws Exception {
        perform(delete("/accounts/1/transactions/100"), NOT_FOUND, managerToken);
    }

    @Test
    @DisplayName("Teste deleteTransaction not found accountId")
    public void testL() throws Exception {
        perform(delete("/accounts/100/transactions/1"), NOT_FOUND, managerToken);
    }

    @Test
    @DisplayName("Teste transferencia not found cpf")
    public void testM() throws Exception {
        MockGen transfer = transaction_transferencia.clone();
        transfer.put("cpf", "111.111.111.55");
        String message = "Pessoa de CPF 111.111.111.55 não encontrada!";
        performException(transfer, post("/accounts/1/transactions"),
                NOT_FOUND, message, clientToken);
    }

    private void testDeposito() throws Exception {
        MockGen savedtransaction = perform(transaction_deposito,
                post("/accounts/1/transactions"), clientToken);
        MockGen transaction = transaction_deposito;

        assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");
        MockGen account = perform(get("/accounts/1"), OK, managerToken);
        setParams(transaction, savedtransaction);

        assertEquals(transaction, savedtransaction);
        assertEquals(10995.44, account.get("saldo"));
    }

    @Test
    public void testSaque() throws Exception {
        MockGen savedtransaction = perform(transaction_saque,
                post("/accounts/1/transactions"), clientToken);
        MockGen transaction = transaction_saque;

        assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");
        MockGen account = perform(get("/accounts/1"), OK, managerToken);
        setParams(transaction, savedtransaction);

        assertEquals(transaction, savedtransaction);
        assertEquals(497.44, account.get("saldo"));
    }

    @Test
    public void testTransferencia() throws Exception {
        MockGen transaction = transaction_transferencia.clone();
        MockGen savedtransaction = perform(transaction,
                post("/accounts/1/transactions"), clientToken);

        assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

        MockGen account1 = perform(get("/accounts/1"), OK, managerToken);
        MockGen account2 = perform(get("/accounts/2"), OK, managerToken);
        setParams(transaction, savedtransaction);
        transaction.remove("cpf");
        assertEquals(transaction, savedtransaction);
        assertEquals(491.12, account1.get("saldo"));
        assertEquals(1500.00, account2.get("saldo"));
    }

    @Test
    public void testPix() throws Exception {
        MockGen transaction = transaction_pix();
        MockGen savedtransaction = perform(transaction,
                post("/accounts/1/transactions"), clientToken);

        assertNotNull(savedtransaction.get("id"), "A resposta deve conter o id da transação criada");

        MockGen account1 = perform(get("/accounts/1"), OK, managerToken);
        MockGen account2 = perform(get("/accounts/2"), OK, managerToken);
        setParams(transaction, savedtransaction);
        transaction.remove("cpf");

        assertEquals(transaction, savedtransaction);
        assertEquals(500.0, account1.get("saldo"));
        assertEquals(1500.00, account2.get("saldo"));
    }

    private void testInsulfficientFoundsenException() throws Exception {
        MockGen transaction = transaction_pix();
        transaction.put("valor", 50000.0);
        String message = "Saldo Insulficiente!";
        performException(transaction,
                post("/accounts/1/transactions"), BAD_REQUEST,
                message, clientToken);
    }

    private void testCreateTransactionAccountIdNotFound() throws Exception {
        String message = "Conta de id 100 não encontrada!";
        performException(transaction_deposito,
                post("/accounts/100/transactions"),
                NOT_FOUND, message, clientToken);
    }

    private MockGen perform(MockGen mockGen, MockHttpServletRequestBuilder builder,
                            String token)
            throws Exception {
        builder = builder.header("Authorization", "Bearer " + token);
        String responseContent =
                mockMvc.perform(builder
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectToJson(mockGen)))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(responseContent, MockGen.class);
    }

    private void performException(MockGen mockGen, MockHttpServletRequestBuilder builder,
                                  HttpStatus status, String message, String token)
            throws Exception {
        builder = builder.header("Authorization", "Bearer " + token);
        mockMvc.perform(builder
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(mockGen)))
                .andExpect(status().is(status.value()))
                .andExpect(jsonPath("$").value(message));
    }

    private MockGen perform(MockHttpServletRequestBuilder builder, HttpStatus expectedStatus,
                            String token)
            throws Exception {
        builder = builder.header("Authorization", "Bearer " + token);
        if (expectedStatus != OK) {
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

    private MockGen[] performGetAll(MockHttpServletRequestBuilder builder,
                                    String token)
            throws Exception {
        builder = builder.header("Authorization", "Bearer " + token);
        String responseContent =
                mockMvc.perform(builder
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().is(OK.value()))
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        return objectMapper.readValue(responseContent, MockGen[].class);
    }

    private void setParams(MockGen request, MockGen response) {
        request.put("id", response.get("id"));
        request.put("code", response.get("code"));
        request.put("createdDate", response.get("createdDate"));
    }

    private LocalDate stringToDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }
}
