package com.harrison.BankAPI.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harrison.BankAPI.mocks.MockGen;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Component
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class TestHelpers implements ApplicationContextAware {

  private static ObjectMapper objectMapper;

  static MockMvc mockMvc;

  @Autowired
  WebApplicationContext wac;

  static String validateToken;

  static {
    try {
      validateToken = createPersonAuthenticate(PersonFixtures.person_admin);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @BeforeEach
  public void setup() {
    mockMvc = MockMvcBuilders
        .webAppContextSetup(wac)
        .apply(springSecurity())
        .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
        .alwaysDo(new SimpleResultHandler())
        .build();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    TestHelpers.objectMapper = applicationContext.getBean(ObjectMapper.class);
  }

  public static String getValidateToken() {
    return validateToken;
  }

  public static String objectToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static MockGen performCreation(MockGen mockGen, String url) throws Exception {
    MockHttpServletRequestBuilder builder = post(url);
    builder = builder.header("Authorization", "Bearer " + validateToken);
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(mockGen)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    return objectMapper.readValue(responseContent, MockGen.class);
  }

  public static MockGen performCreation(MockGen mockGen) throws Exception {
    MockHttpServletRequestBuilder builder = post("/accounts");
    builder = builder.header("Authorization", "Bearer " + validateToken);
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(mockGen)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    return objectMapper.readValue(responseContent, MockGen.class);
  }

  public static String performfind(String url) throws Exception {
    MockHttpServletRequestBuilder builder = get(url);
    builder = builder.header("Authorization", "Bearer " + validateToken);
    return mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();
  }

  public static MockGen performUpdate(MockHttpServletRequestBuilder builder, MockGen mockGen)
      throws Exception {
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(mockGen)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(responseContent, MockGen.class);
  }

  public static void performDelete(MockHttpServletRequestBuilder builder, String message)
      throws Exception {
    mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(message));
  }

  public static void performNotFound(MockHttpServletRequestBuilder builder, String message)
      throws Exception {
    builder = builder.header("Authorization", "Bearer " + validateToken);
    mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").value(message));

  }

  private static boolean isJwt(String token) {
    if (token == null || token.isEmpty()) {
      return false;
    }

    String[] parts = token.split("\\.");

    return parts.length == 3;
  }

  public static String createPersonAuthenticate(MockGen person) throws Exception {
    performCreation(person, "auth/register");

    Map<String, Object> loginInfo = Map.of(
        "username", person.get("username"),
        "password", person.get("password")
    );

    String responseContent =
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(loginInfo)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    LoginResponse loginResponse = objectMapper.readValue(responseContent, LoginResponse.class);
    String token = loginResponse.get("token");

    assertTrue(
        isJwt(token),
        "Resposta da autenticação deve incluir um token JWT válido!"
    );

    return token;
  }

  private static class LoginResponse extends HashMap<String, String> {

    public <K, V> LoginResponse() {
      super();
    }

    public <K, V> LoginResponse(Map<K, V> source) {
      super((Map<String, String>) source);
    }

    public LoginResponse clone() {
      return new LoginResponse(this);
    }
  }

}
