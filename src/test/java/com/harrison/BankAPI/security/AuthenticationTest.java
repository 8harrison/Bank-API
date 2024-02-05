package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class AuthenticationTest {

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
  void testLogin() throws Exception {
    MockGen person = new MockGen(PersonFixtures.person_client);
    performPersonCreation(person);

    testLoginFail(person);
    aux.createPersonAuthenticate(person);
  }

  private void testLoginFail(MockGen person) throws Exception {
    Map<String, Object> loginInfo = Map.of(
        "username", person.get("username"),
        "password", "incorrectPassword"
    );

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJson(loginInfo)))
        .andExpect(status().isForbidden());

    loginInfo = Map.of(
        "username", "nonexistingusername",
        "password", person.get("password")
    );

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJson(loginInfo)))
        .andExpect(status().isForbidden());

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
