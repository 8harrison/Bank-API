package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;
import com.harrison.BankAPI.utils.TransactionFixtures;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class AuthorizationTest {

  MockMvc mockMvc;

  @Autowired
  WebApplicationContext wac;

  @Autowired
  ObjectMapper objectMapper;

  TestHelpers aux = new TestHelpers();

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .apply(springSecurity())
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();

  }

  @Test
  void testAccountAuthorizationcreate() throws Exception {
    String url = "/accounts";

    String json = objectToJson(PersonFixtures.person_client1);

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.CREATED, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.CREATED, json, post(url));
  }

  @Test
  void testAccountAuthorizationGetAll() throws Exception {
    String url = "/accounts";

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testAccountAuthorizationGetById() throws Exception {
    String url = "/accounts/1";

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testAccountAuthorizationGetByCode() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client);
    String url = "/accounts?code=" + account.get("code");

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testAccountAuthorizationUpdate() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client);
    String url = "/accounts/" + account.get("id");

    account.put("email", "pit.moacir@yahool.com");

    String json = objectToJson(account);

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, json, put(url));
  }

  @Test
  void testAccountAuthorizationDelete() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client);
    String url = "/accounts/" + account.get("id");

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, delete(url));
  }

  @Test
  void testTransactionAuthorizationCreate() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client1);
    String url = "/accounts/" + account.get("id") + "/transactions";

    MockGen account1 = aux.performCreation(PersonFixtures.person_client2);

    String json = objectToJson(TransactionFixtures.transaction_deposito);

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.CREATED, json, post(url));
    checkAuthorization(token,
        HttpStatus.FORBIDDEN, json, post("/accounts/" + account1.get("id") + "/transactions/1"));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));
  }

  @Test
  void testTransactionAuthorizationGetById() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client1);
    String url = "/accounts/" + account.get("id") + "/transactions/" + account.get("id");
    MockGen transaction1 = aux.performCreation(TransactionFixtures.transaction_deposito, url);

    MockGen account1 = aux.performCreation(PersonFixtures.person_client2);

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.OK, get(url));
    checkAuthorization(token,
        HttpStatus.FORBIDDEN, get("/accounts/" + account1.get("id") + "/transactions/1"));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testTransactionAuthorizationGetByCode() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client1);
    String url = "/accounts/" + account.get("id") + "/transactions";
    MockGen transaction1 = aux.performCreation(TransactionFixtures.transaction_deposito, url);
    url += "?code=" + transaction1.get("code");
    MockGen account1 = aux.performCreation(PersonFixtures.person_client2);
    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.OK, get(url));
    checkAuthorization(token, HttpStatus.FORBIDDEN,
        get("/accounts/" + account1.get("id") + "/transactions?code=" + transaction1.get("code")));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testTransactionAuthorizationGetAll() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client1);
    String url = "/accounts/" + account.get("id") + "/transactions";

    MockGen person1 = performPersonCreation(PersonFixtures.person_client2);

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.OK, get(url));
    checkAuthorization(token, HttpStatus.FORBIDDEN,
        get("/accounts/" + person1.get("id") + "/transactions/1"));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testTransactionAuthorizationDelete() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client1);
    String url = "/accounts/" + account.get("id") + "/transactions/";
    MockGen transaction1 = aux.performCreation(TransactionFixtures.transaction_deposito, url);

    url += transaction1.get("id");

    MockGen person1 = performPersonCreation(PersonFixtures.person_client2);

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, delete(url));
    checkAuthorization(token, HttpStatus.FORBIDDEN,
        delete("/accounts/" + person1.get("id") + "/transactions/1"));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, delete(url));
  }

  @Test
  void testAccountSetAddressAuthorization() throws Exception {
    MockGen account = aux.performCreation(PersonFixtures.person_client);

    String url = "/accounts/" + account.get("id") + "/address";

    String token = null;

    String json = objectToJson(AddressFixtures.client_address1);

    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.OK, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, json, put(url));
  }

  @Test
  void testBrachAuthorizationCreate() throws Exception {
    String url = "/braches";

    String json = objectToJson(BranchFixtures.branch_1);
    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.CREATED, json, post(url));
  }

  @Test
  void testBrachAuthorizationGetById() throws Exception {
    MockGen branch = aux.performCreation(BranchFixtures.branch_1, "/branches");
    String url = "/branches/" + branch.get("id");

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testBrachAuthorizationGeAll() throws Exception {
    String url = "/branches";

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, get(url));
  }

  @Test
  void testBrachAuthorizationUpdate() throws Exception {
    MockGen branch = aux.performCreation(BranchFixtures.branch_1, "/branches");
    String url = "/branches/" + branch.get("id");
    String json = objectToJson(branch);
    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, json, put(url));
  }

  @Test
  void testBrachAuthorizationDelete() throws Exception {
    MockGen branch = aux.performCreation(BranchFixtures.branch_1, "/branches");
    String url = "/branches/" + branch.get("id");

    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, delete(url));
  }

  @Test
  void testBrachSetAdderessAuthorization() throws Exception {
    MockGen branch = aux.performCreation(BranchFixtures.branch_1);
    String url = "/braches/" + branch.get("id") + "/address";

    String json = objectToJson(AddressFixtures.branch_address1);
    String token = null;

    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(token, HttpStatus.FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(token, HttpStatus.OK, json, put(url));
  }

  private void checkAuthorization(String token, HttpStatus expectedStatus,
      String json, MockHttpServletRequestBuilder builder)
      throws Exception {

    if (token != null) {
      builder = builder.header("Authorization", "Bearer " + token);
    }

    mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().is(expectedStatus.value()));
  }

  private void checkAuthorization(String token, HttpStatus expectedStatus,
      MockHttpServletRequestBuilder builder)
      throws Exception {

    if (token != null) {
      builder = builder.header("Authorization", "Bearer " + token);
    }

    mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is(expectedStatus.value()));
  }

  private MockGen performPersonCreation(MockGen person) throws Exception {
    String url = "/auth/register";

    String responseContent =
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(person)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    return objectMapper.readValue(responseContent, MockGen.class);
  }

}
