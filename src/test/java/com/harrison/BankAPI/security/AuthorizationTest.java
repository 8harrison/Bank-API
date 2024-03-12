package com.harrison.BankAPI.security;

import static com.harrison.BankAPI.utils.AccountFixtures.account_1_request;
import static com.harrison.BankAPI.utils.AddressFixtures.*;
import static com.harrison.BankAPI.utils.BranchFixtures.*;
import static com.harrison.BankAPI.utils.PersonFixtures.*;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static com.harrison.BankAPI.utils.TransactionFixtures.transaction_deposito;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.BankFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;
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

  String adminToken;

  String managerToken;

  @BeforeEach
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .apply(springSecurity())
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();
    adminToken = aux.createPersonAuthenticate(person_admin);
    MockGen manager = person_manager;
    MockGen branch = branch_1;
    managerToken = aux.createPersonAuthenticate(manager);
    perform(BankFixtures.bankMock);
    perform(branch, post("/branches"), managerToken, CREATED);
  }

  private MockGen insertPersonInAccount(MockGen person) throws Exception {
    aux.performCreation(person, "/auth/register");
    MockGen mockPerson = account_1_request.clone();
    mockPerson.put("cpf", person.get("cpf"));
    return mockPerson;
  }

  @Test
  void testAccountAuthorizationcreate() throws Exception {
    String url = "/accounts";
    String json = objectToJson(insertPersonInAccount(person_client1));
    String json1 = objectToJson(insertPersonInAccount(person_client2));
    String token = null;

    checkAuthorization(token, FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(person_admin_1);
    checkAuthorization(token, CREATED, json, post(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, CREATED, json1, post(url));
  }

  @Test
  void testAccountAuthorizationGetAll() throws Exception {
    String url = "/accounts";

    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testAccountAuthorizationGetById() throws Exception {
    MockGen account = insertPersonInAccount(person_client1);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts/" + created.get("id");
    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testAccountAuthorizationGetByCode() throws Exception {
    MockGen account = insertPersonInAccount(person_client1);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts?code=" + created.get("code");

    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testAccountAuthorizationUpdate() throws Exception {
    MockGen account = insertPersonInAccount(person_client1);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts/" + created.get("id");

    created.put("email", "pit.moacir@yahool.com");

    String json = objectToJson(created);

    String token = null;

    checkAuthorization(token, FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, json, put(url));
  }

  @Test
  void testAccountAuthorizationDelete() throws Exception {
    MockGen account = insertPersonInAccount(person_client1);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts/" + created.get("id");

    String token = null;

    checkAuthorization(token, FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, delete(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, delete(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, delete(url));
  }

  @Test
  void testTransactionAuthorizationCreate() throws Exception {
    MockGen account = insertPersonInAccount(person_client);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts/" + created.get("id") + "/transactions";

    MockGen account1 = insertPersonInAccount(person_client2);
    MockGen created1 = perform(account1,
        post("/accounts"),
        adminToken, CREATED);

    String json = objectToJson(transaction_deposito);

    String token = null;

    checkAuthorization(token, FORBIDDEN, json, post(url));

    token = aux.personAuthenticate(person_client);
    checkAuthorization(token, CREATED, json, post(url));
    checkAuthorization(token,
        FORBIDDEN, json, post("/accounts/" + created1.get("id") + "/transactions"));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, FORBIDDEN, json, post(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, FORBIDDEN, json, post(url));
  }

  @Test
  void testTransactionAuthorizationGetById() throws Exception {
    MockGen account = insertPersonInAccount(person_client);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts/" + created.get("id") + "/transactions/1";
    String clientToken = aux.personAuthenticate(person_client);
    perform(transaction_deposito,
        post("/accounts/" + created.get("id") + "/transactions"),
        clientToken, CREATED);

    MockGen account1 = insertPersonInAccount(person_client2);
    MockGen created1 = perform(account1,
        post("/accounts"), adminToken, CREATED);

    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(clientToken, OK, get(url));
    checkAuthorization(clientToken,
        FORBIDDEN, get("/accounts/" + created1.get("id") + "/transactions/1"));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testTransactionAuthorizationGetByCode() throws Exception {
    MockGen account = insertPersonInAccount(person_client);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String clientToken = aux.personAuthenticate(person_client);
    MockGen transaction1 = perform(transaction_deposito,
        post("/accounts/" + created.get("id") + "/transactions"),
        clientToken, CREATED);

    String url = "/accounts/" + created.get("id") + "/transactions?code=" + transaction1.get("code");
    MockGen account1 = insertPersonInAccount(person_client2);
    MockGen created1 = perform(account1,
        post("/accounts"), adminToken, CREATED);
    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(clientToken, OK, get(url));
    checkAuthorization(clientToken, FORBIDDEN,
        get("/accounts/" + created1.get("id") + "/transactions?code=" + transaction1.get("code")));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testTransactionAuthorizationGetAll() throws Exception {
    MockGen account = insertPersonInAccount(person_client);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts/" + created.get("id") + "/transactions";
    String clientToken = aux.personAuthenticate(person_client);


    MockGen account1 = insertPersonInAccount(person_client2);
    MockGen created1 = perform(account1,
        post("/accounts"), adminToken, CREATED);

    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(clientToken, OK, get(url));
    checkAuthorization(clientToken, FORBIDDEN,
        get("/accounts/" + created1.get("id") + "/transactions/1"));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testTransactionAuthorizationDelete() throws Exception {
    MockGen account = insertPersonInAccount(person_client);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String clientToken = aux.personAuthenticate(person_client);
    MockGen transaction1 = perform(transaction_deposito,
        post("/accounts/" + created.get("id") + "/transactions"),
        clientToken, CREATED);

    MockGen transaction2 = perform(transaction_deposito,
        post("/accounts/" + created.get("id") + "/transactions"),
        clientToken, CREATED);

    String url = "/accounts/" + created.get("id") + "/transactions/" + transaction1.get("id");
    MockGen account1 = insertPersonInAccount(person_client2);
    MockGen created1 = perform(account1,
        post("/accounts"), adminToken, CREATED);

    String token = null;

    checkAuthorization(token, FORBIDDEN, delete(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_client);
    checkAuthorization(clientToken, FORBIDDEN, delete(url));
    checkAuthorization(clientToken, FORBIDDEN,
        delete("/accounts/" + created1.get("id") + "/transactions/1"));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, delete(url));
    url = "/accounts/" + created.get("id") + "/transactions/" + transaction2.get("id");
    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, delete(url));
  }

  @Test
  void testAccountSetAddressAuthorization() throws Exception {
    MockGen account = insertPersonInAccount(person_client1);
    MockGen created = perform(account, post("/accounts"), adminToken, CREATED);
    String url = "/accounts/" + created.get("id") + "/address";

    String token = null;

    String json = objectToJson(client_address1);

    checkAuthorization(token, FORBIDDEN, json, put(url));

    token = aux.personAuthenticate(person_client1);
    checkAuthorization(token, FORBIDDEN, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, OK, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, json, put(url));
  }

  @Test
  void testBrachAuthorizationCreate() throws Exception {
    String url = "/branches";

    String json = objectToJson(branch_2);
    String token = null;

    checkAuthorization(token, FORBIDDEN, json, post(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, json, post(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, FORBIDDEN, json, post(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, CREATED, json, post(url));
  }

  @Test
  void testBrachAuthorizationGetById() throws Exception {
    MockGen branch = perform(branch_2, post("/branches"),
        managerToken, CREATED);
    String url = "/branches/" + branch.get("id");

    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testBrachAuthorizationGeAll() throws Exception {
    String url = "/branches";

    String token = null;

    checkAuthorization(token, FORBIDDEN, get(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, FORBIDDEN, get(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, get(url));
  }

  @Test
  void testBrachAuthorizationUpdate() throws Exception {
    MockGen branch = perform(branch_2, post("/branches"),
        managerToken, CREATED);
    String url = "/branches/" + branch.get("id");
    String json = objectToJson(branch);
    String token = null;

    checkAuthorization(token, FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, FORBIDDEN, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, json, put(url));
  }

  @Test
  void testBrachAuthorizationDelete() throws Exception {
    MockGen branch = perform(branch_2, post("/branches"),
        managerToken, CREATED);
    String url = "/branches/" + branch.get("id");

    String token = null;

    checkAuthorization(token, FORBIDDEN, delete(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, delete(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, FORBIDDEN, delete(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, delete(url));
  }

  @Test
  void testBrachSetAdderessAuthorization() throws Exception {
    MockGen branch = perform(branch_2, post("/branches"),
        managerToken, CREATED);
    String url = "/branches/" + branch.get("id") + "/address";

    String json = objectToJson(branch_address1);
    String token = null;

    checkAuthorization(token, FORBIDDEN, json, put(url));

    token = aux.createPersonAuthenticate(person_client);
    checkAuthorization(token, FORBIDDEN, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_admin);
    checkAuthorization(adminToken, FORBIDDEN, json, put(url));

    //token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    checkAuthorization(managerToken, OK, json, put(url));
  }

  private void checkAuthorization(String token, HttpStatus expectedStatus,
      String json, MockHttpServletRequestBuilder builder)
      throws Exception {

    if (token != null) {
      builder = builder.header("Authorization", "Bearer " + token);
    }

    mockMvc.perform(builder.contentType(MediaType.APPLICATION_JSON)
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

  public MockGen perform(MockGen mockGen, MockHttpServletRequestBuilder builder, String token,
      HttpStatus expectedStatus) throws Exception {
    builder = builder.header("Authorization", "Bearer " + token);
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(mockGen)))
            .andExpect(status().is(expectedStatus.value()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    return objectMapper.readValue(responseContent, MockGen.class);
  }

  public void perform(MockGen mockGen) throws Exception {
    MockHttpServletRequestBuilder builder = post("/bank");
    builder = builder.header("Authorization", "Bearer " + managerToken);
    mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJson(mockGen)))
            .andExpect(status().isCreated());
  }

}
