package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.AccountFixtures.*;
import static com.harrison.BankAPI.utils.AddressFixtures.client_address1;
import static com.harrison.BankAPI.utils.BranchFixtures.*;
import static com.harrison.BankAPI.utils.PersonFixtures.*;
import static com.harrison.BankAPI.utils.TestHelpers.objectMapper;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.models.entity.Account;
import com.harrison.BankAPI.service.AccountService;
import com.harrison.BankAPI.utils.AccountFixtures;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
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
public class AccountControllerTest {

    TestHelpers aux = new TestHelpers();

    @Autowired
    WebApplicationContext wac;

    MockGen account;

    MockGen savedAccount;

    String token;
    Timer timer = new Timer();
    @Autowired
    AccountService accountService;

    String managerToken;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .alwaysDo(new SimpleResultHandler())
                .build();
        MockGen bank = new MockGen();
        bank.put("name", "PoupaBank");
        bank.put("incomeTax", 0.005);
        managerToken = aux.createPersonAuthenticate(person_manager);
        perform(bank, post("/bank"), CREATED, managerToken);
        perform(branch_1, post("/branches"), CREATED, managerToken);
        perform(branch_2, post("/branches"), CREATED, managerToken);
        perform(person_client, post("/auth/register"), CREATED, managerToken);
        perform(person_client1, post("/auth/register"), CREATED, managerToken);
        token = aux.createPersonAuthenticate(person_admin_1);
        account = account_1_request;
        savedAccount = Objects.requireNonNull(perform(account, post("/accounts"), CREATED, token)).clone();
    }

    @Test
    public void testCreateAccount() throws InterruptedException {
        assertNotNull(savedAccount.get("id"), "A resposta deve incluir o id da conta criada!");
        assertNotNull(savedAccount.get("code"), "A resposta deve incluir o código da conta!");
        MockGen expectedAccount = account_1_response;
        setParams(expectedAccount);
        expectedAccount.put("address", null);
        assertEquals(expectedAccount, savedAccount);
        timer(0.005);
    }

    @Test
    public void testUpdateTax() throws Exception {
        double tax = 0.007;
        MockGen bank = new MockGen();
        bank.put("incomeTax", tax);
        perform(bank, put("/bank/1"), OK, managerToken);
        timer(tax);
    }

    public void timer(Double tax) throws InterruptedException {
        int base = 6000;
        int delay = base * 10;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Double saldo = (Double) savedAccount.clone().get("saldo");
                MockGen founded = null;
                try {
                    founded = perform(get("/accounts/1"), OK, token);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                double expected = saldo * (1 + tax);
                BigDecimal bd = new BigDecimal(expected).setScale(2, RoundingMode.HALF_EVEN);
                Double response = (Double) founded.get("saldo");
                assertEquals(bd.doubleValue(), response);
            }
        };
        timer.schedule(task, delay);
        Thread.sleep(base * 20);
    }

    @Test
    public void testgetAll() throws Exception {
        List<MockGen> list = List.of(savedAccount,
                Objects.requireNonNull(perform(account_2_request,
                        post("/accounts"), CREATED, token)));
        MockGen[] response = performGetAll(get("/accounts"), HttpStatus.OK, token);
        MockGen[] expected = list.toArray(new MockGen[0]);
        assertArrayEquals(expected, response);
    }

    @Test
    public void testGetById() throws Exception {
        MockGen returnedAccount = perform(get("/accounts/1"), OK, token);

        assertEquals(savedAccount, returnedAccount);
    }

    @Test
    public void testGetByIdNotFound() throws Exception {
        perform(get("/accounts/100"), NOT_FOUND, token);
    }

    @Test
    public void testGetByCode() throws Exception {
        MockGen returnedAccount = perform(
                get("/accounts/find-by-code?code=" + savedAccount.get("code")),
                OK, token);

        assertEquals(savedAccount, returnedAccount);
    }

    @Test
    public void testGetByCodeNotFound() throws Exception {
        perform(get("/accounts/find-by-code?code=0000-00000"), NOT_FOUND, token);
    }

    @Test
    public void testUpdateAccount() throws Exception {
        MockGen updated = account_1_request.clone();
        updated.put("branchCode", "0002");
        updated.remove("saldo");
        MockGen returnedAccount = perform(updated, put("/accounts/" + savedAccount.get("id")),
                OK, token);
        savedAccount.put("code", returnedAccount.get("code"));
        assertEquals(savedAccount, returnedAccount);
    }

    @Test
    public void testUpdateAccountIdNotFound() throws Exception {
        perform(savedAccount, put("/accounts/100"), NOT_FOUND, token);
    }

    @Test
    public void testDeleteAccount() throws Exception {
        aux.performCreation(person_client2, "/auth/register");
        MockGen account = perform(account_3_request, post("/accounts"),
                CREATED, token);
        perform(delete("/accounts/" + account.get("id")), token);
    }

    @Test
    public void testDeleteAccountIdNotFound() throws Exception {
        perform(delete("/accounts/100"), NOT_FOUND, token);

    }

    @Test
    public void testSetAddress() throws Exception {
        MockGen address = client_address1;
        MockGen created = perform(address, put("/accounts/" + savedAccount.get("id") + "/address"),
                OK, token);
        savedAccount.put("address", address);
        assertEquals(savedAccount, created);
    }

    @Test
    public void testCreateAddressNotFoundAccountId() throws Exception {
        MockGen address = client_address1;
        perform(address, put("/accounts/100/address"), HttpStatus.NOT_FOUND, token);
    }

    private MockGen setParams(MockGen request) {
        request.put("id", savedAccount.get("id"));
        request.put("code", savedAccount.get("code"));
        request.put("createdDate", savedAccount.get("createdDate"));
        request.put("lastModifiedDate", savedAccount.get("lastModifiedDate"));
        request.put("createdBy", savedAccount.get("createdBy"));
        request.put("modifiedBy", savedAccount.get("modifiedBy"));
        request.put("cpf", savedAccount.get("cpf"));
        return request;
    }

    private MockGen perform(MockGen mockGen, MockHttpServletRequestBuilder builder,
                            HttpStatus expectedStatus, String token)
            throws Exception {
        builder = builder.header("Authorization", "Bearer " + token);
        if (expectedStatus == NOT_FOUND) {
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

    private MockGen[] performGetAll(MockHttpServletRequestBuilder builder, HttpStatus ok,
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
}
