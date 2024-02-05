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
import com.harrison.BankAPI.utils.AccountFixtures;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
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
    String managerToken = aux.createPersonAuthenticate(person_manager);
    perform(branch_1, post("/branches"), CREATED, managerToken);
    perform(branch_2, post("/branches"), CREATED, managerToken);
    perform(person_client, post("/auth/register"), CREATED, managerToken);
    perform(person_client1, post("/auth/register"), CREATED, managerToken);
    token = aux.createPersonAuthenticate(person_admin_1);
    account = account_1_request;
    savedAccount = perform(account, post("/accounts"), CREATED, token);
  }

  @Test
  public void testCreateAccount() {
    assertNotNull(savedAccount.get("id"), "A resposta deve incluir o id da conta criada!");
    assertNotNull(savedAccount.get("code"), "A resposta deve incluir o código da conta!");
    MockGen expectedAccount = account_1_response;
    expectedAccount = setIdAndCode(expectedAccount, savedAccount);
    expectedAccount.put("address", null);
    assertEquals(expectedAccount, savedAccount);
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
    savedAccount.put("code", "0002-00001");
    MockGen returnedAccount = perform(savedAccount, put("/accounts/" + savedAccount.get("id")),
        OK, token);
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

  private MockGen setIdAndCode(MockGen request, MockGen response) {
    request.put("id", response.get("id"));
    request.put("code", response.get("code"));
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
