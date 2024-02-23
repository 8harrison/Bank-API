package com.harrison.BankAPI.controller;

import static com.harrison.BankAPI.mocks.MockGen.setIdAndCode;
import static com.harrison.BankAPI.utils.TestHelpers.objectMapper;
import static com.harrison.BankAPI.utils.TestHelpers.objectToJson;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.harrison.BankAPI.mocks.MockGen;
import com.harrison.BankAPI.utils.AddressFixtures;
import com.harrison.BankAPI.utils.BranchFixtures;
import com.harrison.BankAPI.utils.PersonFixtures;
import com.harrison.BankAPI.utils.SimpleResultHandler;
import com.harrison.BankAPI.utils.TestHelpers;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
public class BranchControllerTest {

  TestHelpers aux = new TestHelpers();

  @Autowired
  WebApplicationContext wac;

  private MockGen saved;

  private MockGen branch;

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
    branch = BranchFixtures.branch_1;
    token = aux.createPersonAuthenticate(PersonFixtures.person_manager);
    saved = perform(branch, post("/branches"), HttpStatus.CREATED, token);
  }

  @Test
  void testCreate() {
    setParams(saved, branch);
    assertEquals(branch, saved);
  }

  @Test
  void testGetById() throws Exception {
    MockGen founded = perform(get("/branches/1"), HttpStatus.OK, token);
    assertEquals(saved, founded);
  }

  @Test
  void testGetByIdNotFound() throws Exception {
    perform(get("/branches/100"), HttpStatus.NOT_FOUND, token);
  }

  @Test
  void testGetAll() throws Exception {
    List<MockGen> branches = List.of(BranchFixtures.branch_2,
        BranchFixtures.branch_3);
    List<MockGen> expect = new ArrayList<>();
    for (MockGen branch : branches) {
      expect.add(perform(branch, post("/branches"), HttpStatus.CREATED, token));
    }
    expect.add(0, saved);
    MockGen[] response = performGetAll(get("/branches"), token);
    MockGen[] expected = expect.toArray(MockGen[]::new);
    assertArrayEquals(expected, response);
  }

  @Test
  void testUpdate() throws Exception {
    branch = BranchFixtures.branch_1;
    saved = perform(branch, post("/branches"), HttpStatus.CREATED, token);
    saved.put("name", "Agência da Igreja da Matriz");
    MockGen update = perform(saved, put("/branches/" + saved.get("id")), HttpStatus.OK, token);

    assertEquals(saved, update);
  }

  @Test
  void testUpdateNotFound() throws Exception {
    branch.remove("id");
    perform(branch, put("/branches/100"), HttpStatus.NOT_FOUND, token);
  }

  @Test
  void testDelete() throws Exception {
    perform(BranchFixtures.branch_1, post("/branches"), HttpStatus.CREATED, token);
    perform(delete("/branches/1"), token);
  }

  @Test
  void testDeleteNotFound() throws Exception {
    perform(delete("/branches/100"), HttpStatus.NOT_FOUND, token);
  }

  @Test
  void testSetAddress() throws Exception {
    MockGen expect = perform(BranchFixtures.branch_1, post("/branches"), HttpStatus.CREATED, token);
    MockGen response = perform(AddressFixtures.branch_address1,
        put("/branches/%s/address".formatted(expect.get("id"))), HttpStatus.OK, token);
    setIdAndCode(response, expect);

    assertEquals(expect, response);
  }

  @Test
  void testSetAddressNotFound() throws Exception {
    MockGen address = AddressFixtures.branch_address2;
    perform(address, put("/branches/100/address"), HttpStatus.NOT_FOUND, token);
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

  private MockGen[] performGetAll(MockHttpServletRequestBuilder builder, String token)
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

  private MockGen setParams(MockGen response, MockGen request) {
    request.put("id", response.get("id"));
    request.put("code", response.get("code"));
    request.put("address", response.get("address"));
    request.put("createdDate", response.get("createdDate"));
    request.put("lastModifiedDate", response.get("lastModifiedDate"));
    request.put("createdBy", response.get("createdBy"));
    request.put("lastModifiedBy", response.get("lastModifiedBy"));
    return request;
  }
}
