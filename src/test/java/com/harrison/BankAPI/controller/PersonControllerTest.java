package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.w3c.dom.stylesheets.LinkStyle;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class PersonControllerTest {

  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  WebApplicationContext wac;

  TestHelpers aux = new TestHelpers();

  MockGen saved;

  MockGen client;

  @BeforeEach
  public void setup() throws Exception {
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();
    client = PersonFixtures.person_client.clone();
    saved = aux.performCreation(client, "/auth/register");
  }

  @Test
  public void testRegister() throws Exception {
    assertNotNull(saved.get("id"), "A resposta deve incluir o id da pessoa criada");
    setParams();
    assertEquals(client, saved);
  }

  @Test
  public void testGetPersonById() throws Exception {
    String url = "/people/" + saved.get("id");
    MockGen founded = aux.performFind(url);

    assertEquals(saved, founded);
  }

  @Test
  public void testGetAllPeople() throws Exception {
    Object[] list = new Object[]{saved,
        aux.performCreation(PersonFixtures.person_client1, "/auth/register")};
    aux.performFind("/people", list);
  }

  @Test
  public void testUsernameConflicted() throws Exception {
    MockGen person = PersonFixtures.person_client.clone();
    person.put("cpf", "111.111.111-11");
    person.put("email", "qualquercoisa@ponto.com");
    String message = "Já existe um usuário com este Username!";
    aux.perforException(person, post("/auth/register"),
        message, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testInvalidEmailException() throws Exception {
    MockGen person = PersonFixtures.person_client.clone();
    person.put("name", "Bozolino");
    person.put("email", "texto simbolico para teste");
    String message = "Por favor, digite um email válido!";
    aux.perforException(person, post("/auth/register"),
        message, HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testInvalidCpfException() throws Exception {
    MockGen person = PersonFixtures.person_client.clone();
    person.put("name", "Bozolino");
    person.put("cpf", "1111111111");
    String message = "Por favor, digite um CPF válido!";
    aux.perforException(person, post("/auth/register"),
        message, HttpStatus.BAD_REQUEST);
  }

  private void setParams() {
    client.put("id", saved.get("id"));
    client.remove("password");
    client.put("createdDate", saved.get("createdDate"));
    client.put("lastModifiedDate", saved.get("lastModifiedDate"));
  }
}
