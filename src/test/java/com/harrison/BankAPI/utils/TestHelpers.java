package com.harrison.BankAPI.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@Component
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Execution(ExecutionMode.CONCURRENT)
public class TestHelpers implements ApplicationContextAware {

  @Autowired
  public static ObjectMapper objectMapper;

  @Autowired
  static MockMvc mockMvc;

  @Autowired
  WebApplicationContext wac;

  @BeforeEach
  public void setup() throws Exception {
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
    TestHelpers.mockMvc = applicationContext.getBean(MockMvc.class);
  }

  public static String objectToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public MockGen performCreation(MockGen mockGen, String url) throws Exception {
    MockHttpServletRequestBuilder builder = post(url);
    String responseContent =
        mockMvc.perform(builder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(mockGen)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

    return objectMapper.readValue(responseContent, MockGen.class);
  }

  public MockGen performFind(String url) throws Exception {
    MockHttpServletRequestBuilder builder = get(url);
    String responseContent = mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    return objectMapper.readValue(responseContent, MockGen.class);
  }

  public void performFind(String url, Object[] expect) throws Exception {
    MockHttpServletRequestBuilder builder = get(url);
    String responseContent = mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    MockGen[] list = objectMapper.readValue(responseContent, MockGen[].class);
    assertArrayEquals(expect, list);
  }

  public void perforException(MockGen mockGen, MockHttpServletRequestBuilder builder,
      String message, HttpStatus status) throws Exception {
    mockMvc.perform(builder
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectToJson(mockGen)))
        .andExpect(status().is(status.value()))
        .andExpect(jsonPath("$").value(message));
  }

  private static boolean isJwt(String token) {
    if (token == null || token.isEmpty()) {
      return false;
    }

    String[] parts = token.split("\\.");

    return parts.length == 3;
  }

  public String createPersonAuthenticate(MockGen person) throws Exception {
    performCreation(person, "/auth/register");

    Map<String, Object> loginInfo = Map.of(
        "username", person.get("username"),
        "password", person.get("password")
    );

    String responseContent =
        mockMvc.perform(get("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(loginInfo)))
            .andExpect(status().isOk())
            //.andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    mockMvc.perform(get("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectToJson(loginInfo)))
            .andExpect(status().isOk());
    LoginResponse loginResponse = objectMapper.readValue(responseContent, LoginResponse.class);
    String token = loginResponse.get("token");

    assertTrue(
        isJwt(token),
        "Resposta da autenticação deve incluir um token JWT válido!"
    );

    return token;
  }

  public String personAuthenticate(MockGen person) throws Exception {
    Map<String, Object> loginInfo = Map.of(
        "username", person.get("username"),
        "password", person.get("password")
    );
    String responseContent =
        mockMvc.perform(get("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(loginInfo)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

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
